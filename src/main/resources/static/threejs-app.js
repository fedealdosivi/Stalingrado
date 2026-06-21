import { BattleSocket } from './threejs-ws.js';
import { BattleScene } from './threejs-scene.js';
import {
    bindControls, setConnected, setBattleButtonsState,
    updateArmyPanel, logEvent, renderHistory,
} from './threejs-controls.js';

const canvas = document.getElementById('battleCanvas');
const scene = new BattleScene(canvas);
const socket = new BattleSocket();

const state = {
    initialized: false,
    battleInProgress: false,
    battleEnded: false,
    axisCount: 0,
    urssCount: 0,
};

function refreshButtons() {
    setBattleButtonsState(state);
}

bindControls({
    onInit: () => socket.init(),
    onAddSoldiers: (army, type) => {
        if (!state.initialized) {
            logEvent('Initialize armies first!');
            return;
        }
        socket.addSoldiers(army, type);
    },
    onSetBonus: (army, type, value) => {
        if (!state.initialized) return;
        socket.setBonus(army, type, value);
    },
    onStart: () => socket.start(),
    onHistory: () => socket.getHistory(),
});

socket.addEventListener('open', () => setConnected(true));
socket.addEventListener('close', () => setConnected(false));

socket.addEventListener('message', (event) => {
    const data = event.detail;

    switch (data.type) {
        case 'connected':
            console.log('Session ID:', data.sessionId);
            break;

        case 'initialized':
            state.initialized = true;
            state.battleInProgress = false;
            state.battleEnded = false;
            scene.reset();
            updateArmyPanel('AXIS', { count: data.axis.soldiers, attackBonus: data.axis.attackBonus, defenseBonus: data.axis.defenseBonus });
            updateArmyPanel('URSS', { count: data.urss.soldiers, attackBonus: data.urss.attackBonus, defenseBonus: data.urss.defenseBonus });
            state.axisCount = data.axis.soldiers;
            state.urssCount = data.urss.soldiers;
            logEvent('Armies initialized! Add soldiers and start the battle.');
            refreshButtons();
            break;

        case 'soldiersAdded': {
            const army = data.army;
            if (army === 'AXIS') state.axisCount = data.count; else state.urssCount = data.count;
            updateArmyPanel(army, { count: data.count });
            scene.addUnit(army, data.soldierType);
            logEvent(`Added ${data.soldierType} to ${army}. Total: ${data.count}`);
            refreshButtons();
            break;
        }

        case 'bonusSet':
            updateArmyPanel(data.army, { attackBonus: data.attackBonus, defenseBonus: data.defenseBonus });
            break;

        case 'battleStarted':
            state.battleInProgress = true;
            state.battleEnded = false;
            logEvent('=== BATTLE STARTED ===');
            refreshButtons();
            break;

        case 'combatEvent':
            logEvent(data.message);
            handleCombatEvent(data.message);
            break;

        case 'status':
            state.initialized = data.initialized;
            state.battleInProgress = data.battleInProgress;
            updateArmyPanel('AXIS', { count: data.axis.soldiers, attackBonus: data.axis.attackBonus, defenseBonus: data.axis.defenseBonus });
            updateArmyPanel('URSS', { count: data.urss.soldiers, attackBonus: data.urss.attackBonus, defenseBonus: data.urss.defenseBonus });
            state.axisCount = data.axis.soldiers;
            state.urssCount = data.urss.soldiers;
            refreshButtons();
            break;

        case 'history':
            renderHistory(data.battles);
            break;

        case 'error':
            logEvent(`ERROR: ${data.message}`);
            break;
    }
});

// Reuses the same text-parsing contract as the existing 2D UI (app.js):
// combat events carry survivor counts as "Axis: N Urss: N" in plain text.
function handleCombatEvent(message) {
    const match = message.match(/Axis:\s*(\d+)\s*Urss:\s*(\d+)/);
    if (match) {
        const newAxisCount = parseInt(match[1], 10);
        const newUrssCount = parseInt(match[2], 10);
        const axisLost = state.axisCount > newAxisCount;
        const urssLost = state.urssCount > newUrssCount;

        if (axisLost || urssLost) {
            scene.resolveCombat(axisLost ? 'URSS' : 'AXIS');
        }

        state.axisCount = newAxisCount;
        state.urssCount = newUrssCount;
        updateArmyPanel('AXIS', { count: newAxisCount });
        updateArmyPanel('URSS', { count: newUrssCount });
    }

    if (message.includes('Termino la batalla')) {
        state.battleInProgress = false;
        state.battleEnded = true;
        logEvent('=== BATTLE ENDED ===');
        refreshButtons();
    }
}

socket.connect();
