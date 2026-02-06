const { createApp, ref, computed, onMounted, onUnmounted, nextTick } = Vue;

createApp({
    setup() {
        // Connection state
        const connected = ref(false);
        const ws = ref(null);

        // Battle state
        const initialized = ref(false);
        const battleInProgress = ref(false);
        const battleEnded = ref(false);

        // Army stats
        const axisCount = ref(0);
        const axisAttack = ref(2.95);
        const axisDefense = ref(1.0);
        const axisSoldiers = ref([]);

        const urssCount = ref(0);
        const urssAttack = ref(0.95);
        const urssDefense = ref(1.95);
        const urssSoldiers = ref([]);

        // Soldier type colors and display names
        const soldierTypes = {
            fusilero: { color: '#4CAF50', name: 'Rifleman' },
            tanque: { color: '#607D8B', name: 'Tank' },
            avion: { color: '#2196F3', name: 'Aircraft' },
            canon: { color: '#FF9800', name: 'Cannon' },
            trinchero: { color: '#795548', name: 'Trench' },
            cobarde: { color: '#FFEB3B', name: 'Coward' }
        };

        // Combat log
        const combatLog = ref([]);
        const logContainer = ref(null);

        // Battle history
        const battleHistory = ref([]);

        // Battle animation state
        const arenaVisible = ref(false);
        const arenaAxisSoldiers = ref([]);
        const arenaUrssSoldiers = ref([]);
        const currentFight = ref({
            phase: null, // 'moving', 'clash', 'result', null
            axisIndex: -1,
            urssIndex: -1,
            winner: null
        });
        const fightQueue = ref([]);
        const isProcessingFight = ref(false);
        const battleEndPending = ref(false); // Track when battle ended but animations still running

        // Computed
        const canStartBattle = computed(() => {
            return initialized.value && 
                   !battleInProgress.value && 
                   axisCount.value > 0 && 
                   urssCount.value > 0;
        });

        // WebSocket connection
        function connect() {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = `${protocol}//${window.location.host}/ws/battle`;
            
            ws.value = new WebSocket(wsUrl);

            ws.value.onopen = () => {
                connected.value = true;
                console.log('WebSocket connected');
            };

            ws.value.onclose = () => {
                connected.value = false;
                console.log('WebSocket disconnected');
                // Attempt to reconnect after 3 seconds
                setTimeout(connect, 3000);
            };

            ws.value.onerror = (error) => {
                console.error('WebSocket error:', error);
            };

            ws.value.onmessage = (event) => {
                const data = JSON.parse(event.data);
                handleMessage(data);
            };
        }

        function handleMessage(data) {
            console.log('Received:', data);

            switch (data.type) {
                case 'connected':
                    console.log('Session ID:', data.sessionId);
                    break;

                case 'initialized':
                    initialized.value = true;
                    battleInProgress.value = false;
                    battleEnded.value = false;
                    battleEndPending.value = false;
                    arenaVisible.value = false;
                    combatLog.value = [];
                    axisSoldiers.value = [];
                    urssSoldiers.value = [];
                    fightQueue.value = [];
                    isProcessingFight.value = false;
                    updateArmyStats(data);
                    combatLog.value.push('Armies initialized! Add soldiers and start the battle.');
                    break;

                case 'soldiersAdded':
                    if (data.army === 'AXIS') {
                        axisCount.value = data.count;
                        axisSoldiers.value.push(data.soldierType);
                    } else {
                        urssCount.value = data.count;
                        urssSoldiers.value.push(data.soldierType);
                    }
                    combatLog.value.push(`Added ${data.soldierType} to ${data.army}. Total: ${data.count}`);
                    scrollToBottom();
                    break;

                case 'bonusSet':
                    if (data.army === 'AXIS') {
                        axisAttack.value = data.attackBonus;
                        axisDefense.value = data.defenseBonus;
                    } else {
                        urssAttack.value = data.attackBonus;
                        urssDefense.value = data.defenseBonus;
                    }
                    break;

                case 'battleStarted':
                    battleInProgress.value = true;
                    battleEnded.value = false;
                    battleEndPending.value = false;
                    arenaVisible.value = true;
                    // Copy soldiers to arena arrays
                    arenaAxisSoldiers.value = [...axisSoldiers.value];
                    arenaUrssSoldiers.value = [...urssSoldiers.value];
                    fightQueue.value = [];
                    isProcessingFight.value = false;
                    currentFight.value = { phase: null, axisIndex: -1, urssIndex: -1, winner: null };
                    combatLog.value.push('=== BATTLE STARTED ===');
                    scrollToBottom();
                    break;

                case 'combatEvent':
                    combatLog.value.push(data.message);
                    // Update counts from message if it contains survivor info
                    if (data.message.includes('Axis:')) {
                        const match = data.message.match(/Axis:\s*(\d+)\s*Urss:\s*(\d+)/);
                        if (match) {
                            const newAxisCount = parseInt(match[1]);
                            const newUrssCount = parseInt(match[2]);

                            // Determine who lost a soldier
                            const axisLost = axisSoldiers.value.length > newAxisCount;
                            const urssLost = urssSoldiers.value.length > newUrssCount;

                            if (axisLost || urssLost) {
                                // Queue this fight for animation
                                fightQueue.value.push({
                                    winner: axisLost ? 'URSS' : 'AXIS',
                                    newAxisCount,
                                    newUrssCount
                                });
                                // Start processing if not already
                                if (!isProcessingFight.value) {
                                    processNextFight();
                                }
                            }

                            // Update the logical counts (sidebar displays)
                            axisCount.value = newAxisCount;
                            urssCount.value = newUrssCount;
                            // Update soldier arrays
                            while (axisSoldiers.value.length > newAxisCount) {
                                axisSoldiers.value.pop();
                            }
                            while (urssSoldiers.value.length > newUrssCount) {
                                urssSoldiers.value.pop();
                            }
                        }
                    }
                    if (data.message.includes('Termino la batalla')) {
                        battleInProgress.value = false;
                        battleEnded.value = true;
                        combatLog.value.push('=== BATTLE ENDED ===');

                        // Mark battle as pending end - will hide arena when all animations complete
                        battleEndPending.value = true;

                        // If no fights are being processed or queued, hide arena now
                        if (!isProcessingFight.value && fightQueue.value.length === 0) {
                            setTimeout(() => {
                                arenaVisible.value = false;
                                currentFight.value = { phase: null, axisIndex: -1, urssIndex: -1, winner: null };
                                battleEndPending.value = false;
                            }, 2000);
                        }
                    }
                    scrollToBottom();
                    break;

                case 'status':
                    initialized.value = data.initialized;
                    battleInProgress.value = data.battleInProgress;
                    updateArmyStats(data);
                    break;

                case 'history':
                    battleHistory.value = data.battles || [];
                    break;

                case 'error':
                    combatLog.value.push(`ERROR: ${data.message}`);
                    scrollToBottom();
                    break;
            }
        }

        function updateArmyStats(data) {
            if (data.axis) {
                axisCount.value = data.axis.soldiers;
                axisAttack.value = data.axis.attackBonus;
                axisDefense.value = data.axis.defenseBonus;
            }
            if (data.urss) {
                urssCount.value = data.urss.soldiers;
                urssAttack.value = data.urss.attackBonus;
                urssDefense.value = data.urss.defenseBonus;
            }
        }

        function send(action, data = {}) {
            if (ws.value && ws.value.readyState === WebSocket.OPEN) {
                ws.value.send(JSON.stringify({ action, ...data }));
            }
        }

        function scrollToBottom() {
            nextTick(() => {
                if (logContainer.value) {
                    logContainer.value.scrollTop = logContainer.value.scrollHeight;
                }
            });
        }

        // Actions
        function initialize() {
            send('init');
        }

        function addSoldiers(army, type) {
            if (!initialized.value) {
                combatLog.value.push('Initialize armies first!');
                return;
            }
            send('addSoldiers', { army, type, count: 1 });
        }

        function setBonus(army, type, value) {
            if (!initialized.value) return;
            send('setBonus', { army, type, value });
        }

        function startBattle() {
            send('start');
        }

        function loadHistory() {
            send('getHistory');
        }

        function getLogClass(event) {
            if (event.includes('AXIS') || event.includes('Axis')) return 'axis-event';
            if (event.includes('URSS') || event.includes('Urss')) return 'urss-event';
            if (event.includes('===')) return 'battle-marker';
            if (event.includes('ERROR')) return 'error-event';
            return '';
        }

        function getSoldierColor(type) {
            return soldierTypes[type]?.color || '#888888';
        }

        // Process fight animations with delays
        function processNextFight() {
            if (fightQueue.value.length === 0) {
                isProcessingFight.value = false;
                currentFight.value = { phase: null, axisIndex: -1, urssIndex: -1, winner: null };

                // If battle has ended server-side, hide the arena now
                if (battleEndPending.value) {
                    setTimeout(() => {
                        arenaVisible.value = false;
                        battleEndPending.value = false;
                    }, 2000);
                }
                return;
            }

            isProcessingFight.value = true;
            const fight = fightQueue.value.shift();

            // Get the last soldier index from each arena side (the ones fighting)
            const axisIdx = arenaAxisSoldiers.value.length - 1;
            const urssIdx = arenaUrssSoldiers.value.length - 1;

            if (axisIdx < 0 || urssIdx < 0) {
                processNextFight();
                return;
            }

            // Adjust timing based on queue length (faster if many fights queued)
            const queueLength = fightQueue.value.length;
            const speedMultiplier = queueLength > 5 ? 0.5 : queueLength > 2 ? 0.7 : 1;

            const moveTime = Math.round(1000 * speedMultiplier);
            const clashTime = Math.round(1200 * speedMultiplier);
            const resultTime = Math.round(1500 * speedMultiplier);
            const delayTime = Math.round(500 * speedMultiplier);

            // Phase 1: Moving to center
            currentFight.value = {
                phase: 'moving',
                axisIndex: axisIdx,
                urssIndex: urssIdx,
                winner: null
            };

            setTimeout(() => {
                // Phase 2: Clash/Fighting
                currentFight.value = {
                    phase: 'clash',
                    axisIndex: axisIdx,
                    urssIndex: urssIdx,
                    winner: null
                };

                setTimeout(() => {
                    // Phase 3: Result
                    currentFight.value = {
                        phase: 'result',
                        axisIndex: axisIdx,
                        urssIndex: urssIdx,
                        winner: fight.winner
                    };

                    setTimeout(() => {
                        // Remove the loser from arena
                        if (fight.winner === 'URSS') {
                            arenaAxisSoldiers.value.pop();
                        } else {
                            arenaUrssSoldiers.value.pop();
                        }

                        // Reset and process next fight
                        currentFight.value = { phase: null, axisIndex: -1, urssIndex: -1, winner: null };

                        // Delay before next fight
                        setTimeout(() => {
                            processNextFight();
                        }, delayTime);
                    }, resultTime);
                }, clashTime);
            }, moveTime);
        }

        // Lifecycle
        onMounted(() => {
            connect();
        });

        onUnmounted(() => {
            if (ws.value) {
                ws.value.close();
            }
        });

        return {
            // State
            connected,
            initialized,
            battleInProgress,
            battleEnded,
            axisCount,
            axisAttack,
            axisDefense,
            axisSoldiers,
            urssCount,
            urssAttack,
            urssDefense,
            urssSoldiers,
            combatLog,
            logContainer,
            battleHistory,
            canStartBattle,
            soldierTypes,
            arenaVisible,
            arenaAxisSoldiers,
            arenaUrssSoldiers,
            currentFight,

            // Methods
            initialize,
            addSoldiers,
            setBonus,
            startBattle,
            loadHistory,
            getLogClass,
            getSoldierColor
        };
    }
}).mount('#app');
