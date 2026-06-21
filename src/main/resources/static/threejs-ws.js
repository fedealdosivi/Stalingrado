// Thin wrapper around the existing /ws/battle WebSocket contract.
// Reuses the exact same message shapes as app.js (no backend changes needed)
// and re-emits them as plain events so scene/controls code stays decoupled.
export class BattleSocket extends EventTarget {
    constructor() {
        super();
        this.ws = null;
    }

    connect() {
        const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
        const url = `${protocol}//${window.location.host}/ws/battle`;
        this.ws = new WebSocket(url);

        this.ws.onopen = () => this.dispatchEvent(new CustomEvent('open'));
        this.ws.onclose = () => {
            this.dispatchEvent(new CustomEvent('close'));
            setTimeout(() => this.connect(), 3000);
        };
        this.ws.onerror = (err) => console.error('WebSocket error:', err);
        this.ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            this.dispatchEvent(new CustomEvent('message', { detail: data }));
        };
    }

    send(action, data = {}) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify({ action, ...data }));
        }
    }

    init() { this.send('init'); }
    addSoldiers(army, type) { this.send('addSoldiers', { army, type, count: 1 }); }
    setBonus(army, type, value) { this.send('setBonus', { army, type, value }); }
    start() { this.send('start'); }
    getHistory() { this.send('getHistory'); }
}
