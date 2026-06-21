import * as THREE from 'three';
import { OrbitControls } from 'three/addons/controls/OrbitControls.js';

const AXIS_COLOR = 0xff6b6b;
const URSS_COLOR = 0x4ecdc4;

// Same colors as the existing 2D UI legend (app.js soldierTypes), so the two
// front ends stay visually consistent for the same unit type.
const UNIT_COLORS = {
    fusilero: 0x4CAF50,
    tanque: 0x607D8B,
    avion: 0x2196F3,
    canon: 0xFF9800,
    trinchero: 0x795548,
    cobarde: 0xFFEB3B,
};

function material(color) {
    return new THREE.MeshStandardMaterial({ color, transparent: true, opacity: 1 });
}

function buildRifleman(color) {
    const group = new THREE.Group();
    const mat = material(color);
    const body = new THREE.Mesh(new THREE.ConeGeometry(0.35, 0.88, 8), mat);
    body.position.y = 0.64;
    const head = new THREE.Mesh(new THREE.SphereGeometry(0.22, 10, 10), mat);
    head.position.y = 1.2;
    group.add(body, head);
    return group;
}

function buildTank(color) {
    const group = new THREE.Group();
    const mat = material(color);
    const hull = new THREE.Mesh(new THREE.BoxGeometry(1.44, 0.56, 0.88), mat);
    hull.position.y = 0.4;
    const turret = new THREE.Mesh(new THREE.CylinderGeometry(0.29, 0.32, 0.35, 12), mat);
    turret.position.y = 0.83;
    const barrel = new THREE.Mesh(new THREE.CylinderGeometry(0.064, 0.064, 0.88, 8), mat);
    barrel.rotation.z = Math.PI / 2;
    barrel.position.set(0.72, 0.83, 0);
    group.add(hull, turret, barrel);
    return group;
}

function buildAircraft(color) {
    const group = new THREE.Group();
    const mat = material(color);
    const fuselage = new THREE.Mesh(new THREE.ConeGeometry(0.26, 1.2, 6), mat);
    fuselage.rotation.x = Math.PI / 2;
    const wings = new THREE.Mesh(new THREE.BoxGeometry(1.52, 0.064, 0.32), mat);
    group.add(fuselage, wings);
    return group;
}

function buildCannon(color) {
    const group = new THREE.Group();
    const mat = material(color);
    const base = new THREE.Mesh(new THREE.BoxGeometry(0.8, 0.4, 0.8), mat);
    base.position.y = 0.24;
    const barrel = new THREE.Mesh(new THREE.CylinderGeometry(0.112, 0.16, 1.12, 10), mat);
    barrel.rotation.z = Math.PI / 2.6;
    barrel.position.set(0.4, 0.67, 0);
    group.add(base, barrel);
    return group;
}

function buildTrench(color) {
    const group = new THREE.Group();
    const mat = material(color);
    const mound = new THREE.Mesh(new THREE.BoxGeometry(1.6, 0.32, 0.96), mat);
    mound.position.y = 0.16;
    group.add(mound);
    return group;
}

function buildCoward(color) {
    const group = new THREE.Group();
    const mat = material(color);
    const ball = new THREE.Mesh(new THREE.SphereGeometry(0.32, 12, 12), mat);
    ball.position.y = 0.35;
    group.add(ball);
    return group;
}

const BUILDERS = {
    fusilero: buildRifleman,
    tanque: buildTank,
    avion: buildAircraft,
    canon: buildCannon,
    trinchero: buildTrench,
    cobarde: buildCoward,
};

function createUnitMesh(army, type) {
    const group = new THREE.Group();
    const armyColor = army === 'AXIS' ? AXIS_COLOR : URSS_COLOR;

    const baseMat = new THREE.MeshStandardMaterial({
        color: armyColor, emissive: armyColor, emissiveIntensity: 0.35, transparent: true, opacity: 1,
    });
    const base = new THREE.Mesh(new THREE.CylinderGeometry(0.8, 0.8, 0.1, 16), baseMat);
    base.position.y = 0.05;
    group.add(base);

    const build = BUILDERS[type] || buildRifleman;
    const typeColor = UNIT_COLORS[type] ?? 0x888888;
    group.add(build(typeColor));

    return group;
}

// Manages the 3D battlefield: unit meshes, formations, and the fight
// animation queue. Knows nothing about WebSocket messages or DOM controls -
// callers just tell it "add a unit" / "resolve a combat in army X's favor".
export class BattleScene {
    constructor(canvas) {
        this.canvas = canvas;
        this.units = { AXIS: [], URSS: [] };
        this.fightQueue = [];
        this.isProcessingFight = false;

        this._initThree();
        this._tick = this._tick.bind(this);
        requestAnimationFrame(this._tick);
        window.addEventListener('resize', () => this._onResize());
    }

    _initThree() {
        this.scene = new THREE.Scene();
        this.scene.background = new THREE.Color(0x10131f);
        this.scene.fog = new THREE.Fog(0x10131f, 25, 65);

        this.camera = new THREE.PerspectiveCamera(50, 1, 0.1, 200);
        this.camera.position.set(3, 17, 25);

        this.renderer = new THREE.WebGLRenderer({ canvas: this.canvas, antialias: true });
        this.renderer.setPixelRatio(Math.min(window.devicePixelRatio, 2));
        this._onResize();

        this.controls = new OrbitControls(this.camera, this.renderer.domElement);
        this.controls.target.set(0, 0.8, 0);
        this.controls.maxPolarAngle = Math.PI / 2.05;
        this.controls.minDistance = 8;
        this.controls.maxDistance = 55;
        this.controls.update();

        this.scene.add(new THREE.HemisphereLight(0xffffff, 0x222233, 0.9));
        const dir = new THREE.DirectionalLight(0xffffff, 0.8);
        dir.position.set(10, 20, 10);
        this.scene.add(dir);

        const ground = new THREE.Mesh(
            new THREE.PlaneGeometry(60, 30),
            new THREE.MeshStandardMaterial({ color: 0x2b3a2b, roughness: 1 }),
        );
        ground.rotation.x = -Math.PI / 2;
        this.scene.add(ground);

        this._addZoneTint(-15, AXIS_COLOR);
        this._addZoneTint(15, URSS_COLOR);

        const clashStrip = new THREE.Mesh(
            new THREE.PlaneGeometry(4, 28),
            new THREE.MeshBasicMaterial({ color: 0xfeca57, transparent: true, opacity: 0.07 }),
        );
        clashStrip.rotation.x = -Math.PI / 2;
        clashStrip.position.y = 0.005;
        this.scene.add(clashStrip);
    }

    _addZoneTint(x, color) {
        const zone = new THREE.Mesh(
            new THREE.PlaneGeometry(26, 28),
            new THREE.MeshBasicMaterial({ color, transparent: true, opacity: 0.05 }),
        );
        zone.rotation.x = -Math.PI / 2;
        zone.position.set(x, 0.002, 0);
        this.scene.add(zone);
    }

    _onResize() {
        const w = this.canvas.clientWidth, h = this.canvas.clientHeight;
        if (!w || !h) return;
        this.renderer.setSize(w, h, false);
        this.camera.aspect = w / h;
        this.camera.updateProjectionMatrix();
    }

    reset() {
        for (const army of ['AXIS', 'URSS']) {
            for (const unit of this.units[army]) this.scene.remove(unit.mesh);
            this.units[army] = [];
        }
        this.fightQueue = [];
        this.isProcessingFight = false;
    }

    addUnit(army, type) {
        const mesh = createUnitMesh(army, type);
        const unit = {
            army, type, mesh,
            targetScale: 1, currentScale: 0.01,
            targetOpacity: 1, currentOpacity: 1,
            bobSeed: Math.random() * 10,
            fighting: false,
        };
        this.units[army].push(unit);
        this.scene.add(mesh);
        this._layoutFormation(army);

        mesh.position.copy(unit.target).add(new THREE.Vector3(0, 3, 0));
        mesh.scale.setScalar(unit.currentScale);
        return unit;
    }

    // Queues a fight animation resolved in favor of `winnerArmy`. The loser
    // is whichever army is not the winner. Mirrors the existing 2D UI's
    // approach of animating the most-recently-added unit on each side.
    resolveCombat(winnerArmy) {
        this.fightQueue.push({ winnerArmy });
        if (!this.isProcessingFight) this._processNextFight();
    }

    _layoutFormation(army) {
        const list = this.units[army];
        const sign = army === 'AXIS' ? -1 : 1;
        const cols = 6;
        list.forEach((unit, i) => {
            if (unit.fighting) return;
            const row = Math.floor(i / cols);
            const col = i % cols;
            const x = sign * (6.4 + col * 2.6);
            const z = (row - 2.5) * 2.6;
            const y = unit.type === 'avion' ? 3.5 : 0;
            unit.homeY = y;
            unit.target = new THREE.Vector3(x, y, z);
        });
    }

    // Lets callers (e.g. to show a victory banner) wait until the current
    // fight-animation queue has fully drained, instead of firing the instant
    // the server reports the battle as over while units are still animating.
    whenIdle(callback, timeoutMs = 8000) {
        const start = performance.now();
        const check = () => {
            if (!this.isProcessingFight && this.fightQueue.length === 0) {
                callback();
                return;
            }
            if (performance.now() - start > timeoutMs) {
                callback();
                return;
            }
            setTimeout(check, 200);
        };
        check();
    }

    _processNextFight() {
        if (this.fightQueue.length === 0) {
            this.isProcessingFight = false;
            return;
        }
        this.isProcessingFight = true;
        const { winnerArmy } = this.fightQueue.shift();
        const loserArmy = winnerArmy === 'AXIS' ? 'URSS' : 'AXIS';

        const axisUnit = this.units.AXIS[this.units.AXIS.length - 1];
        const urssUnit = this.units.URSS[this.units.URSS.length - 1];
        if (!axisUnit || !urssUnit) {
            this._processNextFight();
            return;
        }
        const winnerUnit = winnerArmy === 'AXIS' ? axisUnit : urssUnit;
        const loserUnit = loserArmy === 'AXIS' ? axisUnit : urssUnit;

        axisUnit.fighting = true;
        urssUnit.fighting = true;

        const speed = this.fightQueue.length > 5 ? 0.5 : this.fightQueue.length > 2 ? 0.7 : 1;
        const moveTime = 700 * speed, clashTime = 500 * speed, resultTime = 600 * speed, gapTime = 300 * speed;

        axisUnit.target = new THREE.Vector3(-1.9, axisUnit.homeY, 0);
        urssUnit.target = new THREE.Vector3(1.9, urssUnit.homeY, 0);

        setTimeout(() => {
            axisUnit.targetScale = 1.4;
            urssUnit.targetScale = 1.4;

            setTimeout(() => {
                winnerUnit.targetScale = 1.6;
                loserUnit.targetScale = 0.3;
                loserUnit.targetOpacity = 0;

                setTimeout(() => {
                    this._removeUnit(loserUnit);
                    winnerUnit.targetScale = 1;
                    winnerUnit.fighting = false;
                    this._layoutFormation(winnerArmy);

                    setTimeout(() => this._processNextFight(), gapTime);
                }, resultTime);
            }, clashTime);
        }, moveTime);
    }

    _removeUnit(unit) {
        const arr = this.units[unit.army];
        const idx = arr.indexOf(unit);
        if (idx !== -1) arr.splice(idx, 1);
        this.scene.remove(unit.mesh);
        this._layoutFormation(unit.army);
    }

    _tick() {
        requestAnimationFrame(this._tick);
        const k = 0.12;
        const now = performance.now();

        for (const army of ['AXIS', 'URSS']) {
            for (const unit of this.units[army]) {
                if (unit.target) unit.mesh.position.lerp(unit.target, k);
                if (unit.type === 'avion' && !unit.fighting) {
                    unit.mesh.position.y += Math.sin(now / 400 + unit.bobSeed) * 0.003;
                }

                unit.currentScale = THREE.MathUtils.lerp(unit.currentScale, unit.targetScale, k);
                unit.mesh.scale.setScalar(unit.currentScale);

                unit.currentOpacity = THREE.MathUtils.lerp(unit.currentOpacity, unit.targetOpacity, k);
                unit.mesh.traverse((child) => {
                    if (child.material) child.material.opacity = unit.currentOpacity;
                });
            }
        }

        this.controls.update();
        this.renderer.render(this.scene, this.camera);
    }
}
