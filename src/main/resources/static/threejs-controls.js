// DOM wiring for the control panel: buttons, sliders, log, history.
// Knows nothing about Three.js or the WebSocket transport - just emits
// user intents via the callbacks passed into `bindControls`, and exposes
// `update*` methods for rendering server-driven state.
export function bindControls({ onInit, onAddSoldiers, onSetBonus, onStart, onHistory }) {
    document.getElementById('btnInit').addEventListener('click', onInit);
    document.getElementById('btnStart').addEventListener('click', onStart);
    document.getElementById('btnHistory').addEventListener('click', onHistory);

    document.querySelectorAll('.unit-buttons button').forEach((btn) => {
        btn.addEventListener('click', () => onAddSoldiers(btn.dataset.army, btn.dataset.type));
    });

    bindBonusSlider('axisAttack', 'axisAttackVal', (v) => onSetBonus('AXIS', 'attack', v));
    bindBonusSlider('axisDefense', 'axisDefenseVal', (v) => onSetBonus('AXIS', 'defense', v));
    bindBonusSlider('urssAttack', 'urssAttackVal', (v) => onSetBonus('URSS', 'attack', v));
    bindBonusSlider('urssDefense', 'urssDefenseVal', (v) => onSetBonus('URSS', 'defense', v));
}

function bindBonusSlider(inputId, labelId, onChange) {
    const input = document.getElementById(inputId);
    const label = document.getElementById(labelId);
    input.addEventListener('input', () => { label.textContent = Number(input.value).toFixed(2); });
    input.addEventListener('change', () => onChange(Number(input.value)));
}

export function setConnected(connected) {
    const el = document.getElementById('connectionStatus');
    el.textContent = connected ? 'Connected' : 'Disconnected';
    el.classList.toggle('connected', connected);
}

export function setBattleButtonsState({ initialized, battleInProgress, axisCount, urssCount }) {
    const btnInit = document.getElementById('btnInit');
    // Only block re-init while a battle is actively running (the backend's
    // army/campo threads are still live) - re-initializing any other time,
    // including right after a fresh init, is always safe.
    btnInit.disabled = battleInProgress;
    btnInit.textContent = initialized ? 'Re-Initialize' : 'Initialize Armies';
    document.getElementById('btnStart').disabled = !(initialized && !battleInProgress && axisCount > 0 && urssCount > 0);
    document.querySelectorAll('.unit-buttons button').forEach((btn) => { btn.disabled = battleInProgress; });
    document.querySelectorAll('.army-summary input[type="range"]').forEach((input) => { input.disabled = battleInProgress; });
}

export function updateArmyPanel(army, { count, attackBonus, defenseBonus }) {
    const prefix = army === 'AXIS' ? 'axis' : 'urss';
    if (count !== undefined) document.getElementById(`${prefix}Count`).textContent = count;
    if (attackBonus !== undefined) {
        document.getElementById(`${prefix}Attack`).value = attackBonus;
        document.getElementById(`${prefix}AttackVal`).textContent = attackBonus.toFixed(2);
    }
    if (defenseBonus !== undefined) {
        document.getElementById(`${prefix}Defense`).value = defenseBonus;
        document.getElementById(`${prefix}DefenseVal`).textContent = defenseBonus.toFixed(2);
    }
}

export function logEvent(message) {
    const container = document.getElementById('logContainer');
    const empty = container.querySelector('.log-empty');
    if (empty) empty.remove();

    const entry = document.createElement('div');
    entry.className = `log-entry ${classifyLogEntry(message)}`;
    entry.textContent = message;
    container.appendChild(entry);
    container.scrollTop = container.scrollHeight;
}

export function showVictory(winnerArmy) {
    const banner = document.getElementById('victoryBanner');
    const text = banner.querySelector('.victory-text');
    text.textContent = `${winnerArmy} WINS!`;
    text.className = `victory-text ${winnerArmy === 'AXIS' ? 'axis' : 'urss'}`;
    banner.classList.add('show');
}

export function hideVictory() {
    document.getElementById('victoryBanner').classList.remove('show');
}

function classifyLogEntry(message) {
    if (message.includes('===')) return 'battle-marker';
    if (message.toUpperCase().includes('ERROR')) return 'error-event';
    if (message.includes('AXIS') || message.includes('Axis')) return 'axis-event';
    if (message.includes('URSS') || message.includes('Urss')) return 'urss-event';
    return '';
}

export function renderHistory(battles) {
    const list = document.getElementById('historyList');
    list.innerHTML = '';
    if (!battles || battles.length === 0) {
        list.innerHTML = '<div class="history-empty">No battle history loaded.</div>';
        return;
    }
    for (const battle of battles) {
        const item = document.createElement('div');
        item.className = 'history-item';
        item.innerHTML = `<strong>Battle #${battle.id}</strong><p></p>`;
        item.querySelector('p').textContent = battle.resultado || '';
        list.appendChild(item);
    }
}
