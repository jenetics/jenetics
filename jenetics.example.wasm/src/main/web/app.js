/**
 * Jenetics Evolving Images - WebAssembly Application Controller
 */
(async function () {
    'use strict';

    const STORAGE_KEY = 'io.jenetics.example.wasm.engineParam';

    // DOM Elements
    const statusBanner = document.getElementById('statusBanner');
    const sourceCanvas = document.getElementById('sourceCanvas');
    const polygonCanvas = document.getElementById('polygonCanvas');
    const sourceCtx = sourceCanvas.getContext('2d');
    const polygonCtx = polygonCanvas.getContext('2d');

    const startBtn = document.getElementById('startBtn');
    const pauseBtn = document.getElementById('pauseBtn');
    const stopBtn = document.getElementById('stopBtn');
    const openBtn = document.getElementById('openBtn');
    const saveBtn = document.getElementById('saveBtn');
    const fileInput = document.getElementById('fileInput');

    const curGen = document.getElementById('curGen');
    const curFit = document.getElementById('curFit');
    const curAge = document.getElementById('curAge');
    const curEval = document.getElementById('curEval');

    const bestGen = document.getElementById('bestGen');
    const bestFit = document.getElementById('bestFit');
    const bestAge = document.getElementById('bestAge');
    const bestEval = document.getElementById('bestEval');
    const genPerSec = document.getElementById('genPerSec');

    const paramPopSize = document.getElementById('paramPopSize');
    const paramTournSize = document.getElementById('paramTournSize');
    const paramMutRate = document.getElementById('paramMutRate');
    const paramMutMult = document.getElementById('paramMutMult');
    const paramPolyLength = document.getElementById('paramPolyLength');
    const paramPolyCount = document.getElementById('paramPolyCount');
    const paramRefWidth = document.getElementById('paramRefWidth');
    const paramRefHeight = document.getElementById('paramRefHeight');
    const paramError = document.getElementById('paramError');
    const paramInputs = [
        paramPopSize, paramTournSize, paramMutRate, paramMutMult,
        paramPolyLength, paramPolyCount, paramRefWidth, paramRefHeight
    ];

    // State
    const State = { IDLE: 'IDLE', RUNNING: 'RUNNING', PAUSED: 'PAUSED' };
    let currentState = State.IDLE;
    let app = null;
    let sourceImage = null;
    let animationFrameId = null;
    let batchSize = 1;

    function createAppProxy(handler) {
        const call = (op, args = {}) => {
            const response = handler({op, ...args});
            return response && response.value !== undefined ? response.value : undefined;
        };
        return {
            init: (populationSize, tournamentSize, mutationRate, mutationMultitude, polygonLength, polygonCount, referenceWidth, referenceHeight, refRgba) => call('init', {
                populationSize, tournamentSize, mutationRate, mutationMultitude,
                polygonLength, polygonCount, referenceWidth, referenceHeight, refRgba
            }),
            evolve: (generations) => call('evolve', {generations}),
            generation: () => call('generation'),
            bestGeneration: () => call('bestGeneration'),
            currentFitness: () => call('currentFitness'),
            bestFitness: () => call('bestFitness'),
            currentPopulationAge: () => call('currentPopulationAge'),
            bestPopulationAge: () => call('bestPopulationAge'),
            currentEvaluationMillis: () => call('currentEvaluationMillis'),
            bestEvaluationMillis: () => call('bestEvaluationMillis'),
            polygonCount: () => call('polygonCount'),
            polygonLength: () => call('polygonLength'),
            referenceWidth: () => call('referenceWidth'),
            referenceHeight: () => call('referenceHeight'),
            bestPolygons: () => call('bestPolygons'),
            renderBest: (width, height) => call('renderBest', {width, height}),
            status: () => call('status')
        };
    }

    const PARAM_BOUNDS = {
        populationSize: [3, 2147483647],
        tournamentSize: [2, 2147483647],
        mutationRate: [0.0, 1.0],
        mutationMultitude: [0.0, 1.0],
        polygonLength: [3, 2147483647],
        polygonCount: [2, 2147483647],
        referenceWidth: [2, 32767],
        referenceHeight: [2, 32767]
    };

    // Speed tracking
    let lastGen = 0;
    let lastSpeedTime = performance.now();

    // Load persisted parameters or defaults
    function loadSavedParameters() {
        try {
            const saved = localStorage.getItem(STORAGE_KEY);
            if (saved) {
                const p = JSON.parse(saved);
                if (p.populationSize !== undefined) paramPopSize.value = p.populationSize;
                if (p.tournamentSize !== undefined) paramTournSize.value = p.tournamentSize;
                if (p.mutationRate !== undefined) paramMutRate.value = p.mutationRate;
                if (p.mutationMultitude !== undefined) paramMutMult.value = p.mutationMultitude;
                if (p.polygonLength !== undefined) paramPolyLength.value = p.polygonLength;
                if (p.polygonCount !== undefined) paramPolyCount.value = p.polygonCount;
                if (p.referenceWidth !== undefined) paramRefWidth.value = p.referenceWidth;
                if (p.referenceHeight !== undefined) paramRefHeight.value = p.referenceHeight;
            }
        } catch (e) {
            console.warn('Could not load saved parameters:', e);
        }
    }

    function saveParameters(params) {
        try {
            localStorage.setItem(STORAGE_KEY, JSON.stringify(params));
        } catch (e) {
            console.warn('Could not save parameters:', e);
        }
    }

    function getValidatedParameters() {
        paramError.textContent = '';
        const popSize = parseInt(paramPopSize.value, 10);
        const tournSize = parseInt(paramTournSize.value, 10);
        const mutRate = parseFloat(paramMutRate.value);
        const mutMult = parseFloat(paramMutMult.value);
        const polyLength = parseInt(paramPolyLength.value, 10);
        const polyCount = parseInt(paramPolyCount.value, 10);
        const refWidth = parseInt(paramRefWidth.value, 10);
        const refHeight = parseInt(paramRefHeight.value, 10);

        if (isNaN(popSize) || popSize < PARAM_BOUNDS.populationSize[0] || popSize > PARAM_BOUNDS.populationSize[1]) {
            paramError.textContent = `Population size must be between ${PARAM_BOUNDS.populationSize[0]} and ${PARAM_BOUNDS.populationSize[1]}.`;
            return null;
        }
        if (isNaN(tournSize) || tournSize < PARAM_BOUNDS.tournamentSize[0] || tournSize > PARAM_BOUNDS.tournamentSize[1]) {
            paramError.textContent = `Tournament size must be between ${PARAM_BOUNDS.tournamentSize[0]} and ${PARAM_BOUNDS.tournamentSize[1]}.`;
            return null;
        }
        if (isNaN(mutRate) || mutRate < PARAM_BOUNDS.mutationRate[0] || mutRate > PARAM_BOUNDS.mutationRate[1]) {
            paramError.textContent = `Mutation rate must be between ${PARAM_BOUNDS.mutationRate[0]} and ${PARAM_BOUNDS.mutationRate[1]}.`;
            return null;
        }
        if (isNaN(mutMult) || mutMult < PARAM_BOUNDS.mutationMultitude[0] || mutMult > PARAM_BOUNDS.mutationMultitude[1]) {
            paramError.textContent = `Mutation multitude must be between ${PARAM_BOUNDS.mutationMultitude[0]} and ${PARAM_BOUNDS.mutationMultitude[1]}.`;
            return null;
        }
        if (isNaN(polyLength) || polyLength < PARAM_BOUNDS.polygonLength[0] || polyLength > PARAM_BOUNDS.polygonLength[1]) {
            paramError.textContent = `Polygon length must be between ${PARAM_BOUNDS.polygonLength[0]} and ${PARAM_BOUNDS.polygonLength[1]}.`;
            return null;
        }
        if (isNaN(polyCount) || polyCount < PARAM_BOUNDS.polygonCount[0] || polyCount > PARAM_BOUNDS.polygonCount[1]) {
            paramError.textContent = `Polygon count must be between ${PARAM_BOUNDS.polygonCount[0]} and ${PARAM_BOUNDS.polygonCount[1]}.`;
            return null;
        }
        if (isNaN(refWidth) || refWidth < PARAM_BOUNDS.referenceWidth[0] || refWidth > PARAM_BOUNDS.referenceWidth[1]) {
            paramError.textContent = `Reference width must be between ${PARAM_BOUNDS.referenceWidth[0]} and ${PARAM_BOUNDS.referenceWidth[1]}.`;
            return null;
        }
        if (isNaN(refHeight) || refHeight < PARAM_BOUNDS.referenceHeight[0] || refHeight > PARAM_BOUNDS.referenceHeight[1]) {
            paramError.textContent = `Reference height must be between ${PARAM_BOUNDS.referenceHeight[0]} and ${PARAM_BOUNDS.referenceHeight[1]}.`;
            return null;
        }

        const params = {
            populationSize: popSize,
            tournamentSize: tournSize,
            mutationRate: mutRate,
            mutationMultitude: mutMult,
            polygonLength: polyLength,
            polygonCount: polyCount,
            referenceWidth: refWidth,
            referenceHeight: refHeight
        };
        saveParameters(params);
        return params;
    }

    function setUIState(state) {
        currentState = state;
        if (state === State.IDLE) {
            startBtn.disabled = !sourceImage || !app;
            pauseBtn.disabled = true;
            pauseBtn.textContent = 'Pause';
            stopBtn.disabled = true;
            openBtn.disabled = false;
            saveBtn.disabled = !app || app.generation() === 0;
            paramInputs.forEach(input => (input.disabled = false));
        } else if (state === State.RUNNING) {
            startBtn.disabled = true;
            pauseBtn.disabled = false;
            pauseBtn.textContent = 'Pause';
            stopBtn.disabled = false;
            openBtn.disabled = true;
            saveBtn.disabled = false;
            paramInputs.forEach(input => (input.disabled = true));
        } else if (state === State.PAUSED) {
            startBtn.disabled = true;
            pauseBtn.disabled = false;
            pauseBtn.textContent = 'Resume';
            stopBtn.disabled = false;
            openBtn.disabled = true;
            saveBtn.disabled = false;
            paramInputs.forEach(input => (input.disabled = true));
        }
    }

    function drawSourceImage(img) {
        sourceImage = img;
        const w = img.naturalWidth || img.width;
        const h = img.naturalHeight || img.height;
        const aspect = w / h;

        const maxDim = 300;
        let dw = maxDim;
        let dh = maxDim;
        if (aspect >= 1) {
            dh = Math.round(maxDim / aspect);
        } else {
            dw = Math.round(maxDim * aspect);
        }

        sourceCanvas.width = dw;
        sourceCanvas.height = dh;
        polygonCanvas.width = dw;
        polygonCanvas.height = dh;

        sourceCtx.clearRect(0, 0, dw, dh);
        sourceCtx.drawImage(img, 0, 0, dw, dh);

        polygonCtx.fillStyle = '#FFFFFF';
        polygonCtx.fillRect(0, 0, dw, dh);

        setUIState(State.IDLE);
    }

    function extractReferencePixels(img, width, height) {
        const offscreen = document.createElement('canvas');
        offscreen.width = width;
        offscreen.height = height;
        const ctx = offscreen.getContext('2d');
        ctx.drawImage(img, 0, 0, width, height);
        const imgData = ctx.getImageData(0, 0, width, height);

        const refRgba = new Int32Array(imgData.data.length);
        for (let i = 0; i < imgData.data.length; i++) {
            refRgba[i] = imgData.data[i];
        }
        return refRgba;
    }

    function drawPolygons(polygons, polygonCount, polygonLength, width, height, ctx) {
        ctx.fillStyle = '#FFFFFF';
        ctx.fillRect(0, 0, width, height);

        if (!polygons || polygons.length === 0) {
            return;
        }

        const polyStride = 4 + 2 * polygonLength;
        for (let i = 0; i < polygonCount; i++) {
            const offset = i * polyStride;
            if (offset + polyStride > polygons.length) break;

            const r = Math.round(polygons[offset] * 255);
            const g = Math.round(polygons[offset + 1] * 255);
            const b = Math.round(polygons[offset + 2] * 255);
            const a = polygons[offset + 3];

            ctx.fillStyle = `rgba(${r}, ${g}, ${b}, ${a})`;
            ctx.beginPath();
            for (let j = 0; j < polygonLength; j++) {
                const px = polygons[offset + 4 + j * 2] * width;
                const py = polygons[offset + 4 + j * 2 + 1] * height;
                if (j === 0) {
                    ctx.moveTo(px, py);
                } else {
                    ctx.lineTo(px, py);
                }
            }
            ctx.closePath();
            ctx.fill();
        }
    }

    function updateStats() {
        if (!app) return;
        const curG = app.generation();
        curGen.textContent = curG;
        curFit.textContent = app.currentFitness().toFixed(5);
        curAge.textContent = app.currentPopulationAge().toFixed(1);
        curEval.textContent = app.currentEvaluationMillis().toFixed(1) + ' ms';

        bestGen.textContent = app.bestGeneration();
        bestFit.textContent = app.bestFitness().toFixed(5);
        bestAge.textContent = app.bestPopulationAge().toFixed(1);
        bestEval.textContent = app.bestEvaluationMillis().toFixed(1) + ' ms';

        const now = performance.now();
        const elapsed = (now - lastSpeedTime) / 1000.0;
        if (elapsed >= 0.5) {
            const speed = (curG - lastGen) / elapsed;
            genPerSec.textContent = speed.toFixed(1);
            lastGen = curG;
            lastSpeedTime = now;
        }
    }

    function tick() {
        if (currentState !== State.RUNNING) {
            return;
        }

        const t0 = performance.now();
        app.evolve(batchSize);
        const dt = performance.now() - t0;

        // Adapt batch size to maintain ~20ms per frame
        if (dt < 15 && batchSize < 50) {
            batchSize = Math.min(50, batchSize + 1);
        } else if (dt > 35 && batchSize > 1) {
            batchSize = Math.max(1, batchSize - 1);
        }

        const polygons = app.bestPolygons();
        drawPolygons(polygons, app.polygonCount(), app.polygonLength(), polygonCanvas.width, polygonCanvas.height, polygonCtx);
        updateStats();

        if (currentState === State.RUNNING) {
            animationFrameId = requestAnimationFrame(tick);
        }
    }

    function startEvolution() {
        const params = getValidatedParameters();
        if (!params || !sourceImage || !app) {
            return;
        }

        const refRgba = extractReferencePixels(sourceImage, params.referenceWidth, params.referenceHeight);
        app.init(
            params.populationSize,
            params.tournamentSize,
            params.mutationRate,
            params.mutationMultitude,
            params.polygonLength,
            params.polygonCount,
            params.referenceWidth,
            params.referenceHeight,
            refRgba
        );

        lastGen = 0;
        lastSpeedTime = performance.now();
        batchSize = 1;
        setUIState(State.RUNNING);
        animationFrameId = requestAnimationFrame(tick);
    }

    function pauseEvolution() {
        if (currentState === State.RUNNING) {
            if (animationFrameId) {
                cancelAnimationFrame(animationFrameId);
                animationFrameId = null;
            }
            setUIState(State.PAUSED);
        } else if (currentState === State.PAUSED) {
            setUIState(State.RUNNING);
            lastSpeedTime = performance.now();
            lastGen = app.generation();
            animationFrameId = requestAnimationFrame(tick);
        }
    }

    function stopEvolution() {
        if (animationFrameId) {
            cancelAnimationFrame(animationFrameId);
            animationFrameId = null;
        }
        setUIState(State.IDLE);
    }

    function handleImageFile(file) {
        if (!file || !file.type.startsWith('image/')) return;
        const reader = new FileReader();
        reader.onload = (e) => {
            const img = new Image();
            img.onload = () => {
                drawSourceImage(img);
                if (currentState === State.RUNNING || currentState === State.PAUSED) {
                    stopEvolution();
                }
            };
            img.src = e.target.result;
        };
        reader.readAsDataURL(file);
    }

    function saveImageAsPng() {
        if (!app || app.generation() === 0) return;
        const polygons = app.bestPolygons();
        if (!polygons || polygons.length === 0) return;

        const w = sourceCanvas.width;
        const h = sourceCanvas.height;
        const scale = Math.max(500 / w, 500 / h, 1);
        const exportWidth = Math.round(w * scale);
        const exportHeight = Math.round(h * scale);

        const offscreen = document.createElement('canvas');
        offscreen.width = exportWidth;
        offscreen.height = exportHeight;
        const ctx = offscreen.getContext('2d');

        drawPolygons(polygons, app.polygonCount(), app.polygonLength(), exportWidth, exportHeight, ctx);

        offscreen.toBlob((blob) => {
            if (!blob) return;
            const url = URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = 'evolving-image.png';
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);
        }, 'image/png');
    }

    // Event Listeners
    startBtn.addEventListener('click', startEvolution);
    pauseBtn.addEventListener('click', pauseEvolution);
    stopBtn.addEventListener('click', stopEvolution);
    openBtn.addEventListener('click', () => fileInput.click());
    fileInput.addEventListener('change', (e) => {
        if (e.target.files && e.target.files[0]) {
            handleImageFile(e.target.files[0]);
        }
    });
    saveBtn.addEventListener('click', saveImageAsPng);
    window.addEventListener('resize', () => {
        if (app && app.generation() > 0) {
            drawPolygons(
                app.bestPolygons(),
                app.polygonCount(),
                app.polygonLength(),
                polygonCanvas.width,
                polygonCanvas.height,
                polygonCtx
            );
        }
    });

    // Initialize WebAssembly and load default image
    try {
        loadSavedParameters();
        statusBanner.textContent = 'Initializing WebAssembly runtime...';

        await (globalThis.__evolvingImagesRun || GraalVM.run([]));
        const handler = globalThis.evolvingImagesCall;
        if (!handler) {
            throw new Error('EvolvingImagesCall export not available.');
        }
        app = createAppProxy(handler);

        statusBanner.textContent = 'WebAssembly ready. Loading default sample image...';

        const defaultImg = new Image();
        defaultImg.onload = () => {
            drawSourceImage(defaultImg);
            statusBanner.textContent = 'Ready. Click "Start" to begin evolution.';
        };
        defaultImg.onerror = () => {
            statusBanner.textContent = 'Ready. Please open an image to start.';
            setUIState(State.IDLE);
        };
        defaultImg.src = 'monalisa.png';
    } catch (err) {
        console.error('Initialization error:', err);
        statusBanner.textContent = 'Error initializing WebAssembly: ' + err.message;
        statusBanner.style.backgroundColor = '#fee2e2';
        statusBanner.style.color = '#991b1b';
    }
})();
