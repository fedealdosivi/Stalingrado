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
        
        const urssCount = ref(0);
        const urssAttack = ref(0.95);
        const urssDefense = ref(1.95);

        // Combat log
        const combatLog = ref([]);
        const logContainer = ref(null);

        // Battle history
        const battleHistory = ref([]);

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
                    updateArmyStats(data);
                    combatLog.value.push('Armies initialized! Add soldiers and start the battle.');
                    break;

                case 'soldiersAdded':
                    if (data.army === 'AXIS') {
                        axisCount.value = data.count;
                    } else {
                        urssCount.value = data.count;
                    }
                    combatLog.value.push(`Added ${data.type} to ${data.army}. Total: ${data.count}`);
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
                    combatLog.value.push('=== BATTLE STARTED ===');
                    scrollToBottom();
                    break;

                case 'combatEvent':
                    combatLog.value.push(data.message);
                    // Update counts from message if it contains survivor info
                    if (data.message.includes('Axis:')) {
                        const match = data.message.match(/Axis:\s*(\d+)\s*Urss:\s*(\d+)/);
                        if (match) {
                            axisCount.value = parseInt(match[1]);
                            urssCount.value = parseInt(match[2]);
                        }
                    }
                    if (data.message.includes('Termino la batalla')) {
                        battleInProgress.value = false;
                        battleEnded.value = true;
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
            urssCount,
            urssAttack,
            urssDefense,
            combatLog,
            logContainer,
            battleHistory,
            canStartBattle,
            
            // Methods
            initialize,
            addSoldiers,
            setBonus,
            startBattle,
            loadHistory,
            getLogClass
        };
    }
}).mount('#app');
