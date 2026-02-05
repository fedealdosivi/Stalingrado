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
        const currentBattle = ref({
            active: false,
            axisColor: '#4CAF50',
            urssColor: '#4CAF50',
            winner: null,
            showResult: false
        });

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
                    combatLog.value = [];
                    axisSoldiers.value = [];
                    urssSoldiers.value = [];
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
                    // Initialize battle arena with first soldiers from each army
                    const startAxisType = axisSoldiers.value.length > 0 ? axisSoldiers.value[0] : 'fusilero';
                    const startUrssType = urssSoldiers.value.length > 0 ? urssSoldiers.value[0] : 'fusilero';
                    currentBattle.value = {
                        active: true,
                        axisColor: soldierTypes[startAxisType]?.color || '#4CAF50',
                        urssColor: soldierTypes[startUrssType]?.color || '#4CAF50',
                        winner: null,
                        showResult: false
                    };
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

                            // Determine who lost a soldier and trigger animation
                            const axisLost = axisSoldiers.value.length > newAxisCount;
                            const urssLost = urssSoldiers.value.length > newUrssCount;

                            if (axisLost || urssLost) {
                                // Get colors for the fighting soldiers (the ones about to be removed)
                                const fightAxisType = axisSoldiers.value.length > 0 ? axisSoldiers.value[axisSoldiers.value.length - 1] : 'fusilero';
                                const fightUrssType = urssSoldiers.value.length > 0 ? urssSoldiers.value[urssSoldiers.value.length - 1] : 'fusilero';

                                // Show the fight result
                                currentBattle.value = {
                                    active: true,
                                    axisColor: soldierTypes[fightAxisType]?.color || '#4CAF50',
                                    urssColor: soldierTypes[fightUrssType]?.color || '#4CAF50',
                                    winner: axisLost ? 'URSS' : 'AXIS',
                                    showResult: true
                                };

                                // After animation, go back to fighting state with next soldiers
                                setTimeout(() => {
                                    if (battleInProgress.value && newAxisCount > 0 && newUrssCount > 0) {
                                        const nextAxisType = axisSoldiers.value.length > 1 ? axisSoldiers.value[axisSoldiers.value.length - 2] : 'fusilero';
                                        const nextUrssType = urssSoldiers.value.length > 1 ? urssSoldiers.value[urssSoldiers.value.length - 2] : 'fusilero';
                                        currentBattle.value = {
                                            active: true,
                                            axisColor: soldierTypes[nextAxisType]?.color || '#4CAF50',
                                            urssColor: soldierTypes[nextUrssType]?.color || '#4CAF50',
                                            winner: null,
                                            showResult: false
                                        };
                                    }
                                }, 600);
                            }

                            // Remove soldiers from visual if count decreased
                            while (axisSoldiers.value.length > newAxisCount) {
                                axisSoldiers.value.pop();
                            }
                            while (urssSoldiers.value.length > newUrssCount) {
                                urssSoldiers.value.pop();
                            }
                            axisCount.value = newAxisCount;
                            urssCount.value = newUrssCount;
                        }
                    }
                    if (data.message.includes('Termino la batalla')) {
                        battleInProgress.value = false;
                        battleEnded.value = true;
                        // Show final winner
                        setTimeout(() => {
                            currentBattle.value = { active: false, axisColor: '#4CAF50', urssColor: '#4CAF50', winner: null, showResult: false };
                        }, 1000);
                        combatLog.value.push('=== BATTLE ENDED ===');
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
            currentBattle,

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
