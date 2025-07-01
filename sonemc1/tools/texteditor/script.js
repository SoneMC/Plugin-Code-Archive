const colorMap = {
    '&0': '#000000', '&1': '#0000AA', '&2': '#00AA00', '&3': '#00AAAA', '&4': '#AA0000',
    '&5': '#AA00AA', '&6': '#FFAA00', '&7': '#AAAAAA', '&8': '#555555', '&9': '#5555FF',
    '&a': '#55FF55', '&b': '#55FFFF', '&c': '#FF5555', '&d': '#FF55FF', '&e': '#FFFF55', '&f': '#FFFFFF'
};

const formatMap = {
    '&l': { name: 'Bold', style: 'font-weight: bold;' },
    '&n': { name: 'Underline', style: 'text-decoration: underline;' },
    '&o': { name: 'Italic', style: 'font-style: italic;' },
    '&m': { name: 'Strikethrough', style: 'text-decoration: line-through;' },
    '&r': { name: 'Reset', style: '' },
    '&k': { name: 'Obfuscated', style: '' }
};

const basicColorsContainer = document.getElementById('basicColors');
const lightColorsContainer = document.getElementById('lightColors');
const stylesContainer = document.getElementById('styles');
const outputBox = document.getElementById('outputBox');
const inputBox = document.getElementById('inputBox');

Object.entries(colorMap).forEach(([code, color]) => {
    const div = document.createElement('div');
    div.className = 'color-code';
    div.textContent = code;
    div.style.backgroundColor = color;
    div.style.color = (['&0', '&8'].includes(code)) ? 'white' : 'black';
    div.onclick = () => insertColorCode(code);
    if (['&a', '&b', '&c', '&d', '&e', '&f'].includes(code)) {
        lightColorsContainer.appendChild(div);
    } else {
        basicColorsContainer.appendChild(div);
    }
});

Object.entries(formatMap).forEach(([code, data]) => {
    const div = document.createElement('div');
    div.className = 'color-code tooltip';
    div.textContent = code;
    div.style.backgroundColor = '#333';
    div.style.color = 'white';

    const tooltip = document.createElement('span');
    tooltip.className = 'tooltip-text';
    tooltip.textContent = data.name;

    div.appendChild(tooltip);
    div.onclick = () => insertColorCode(code);
    stylesContainer.appendChild(div);
});

function insertColorCode(code) {
    let start = inputBox.selectionStart;
    let end = inputBox.selectionEnd;
    let text = inputBox.value;

    inputBox.value = text.substring(0, start) + code + text.substring(end);
    inputBox.selectionStart = inputBox.selectionEnd = start + code.length;
    inputBox.focus();
    inputBox.dispatchEvent(new Event('input'));
}

inputBox.addEventListener('input', function () {
    let inputText = this.value;
    let outputHTML = '';
    let lines = inputText.split('\n');
    let defaultColor = '#FFFFFF';

    lines.forEach((line, index) => {
        let segments = line.split(/(&[0-9a-fA-Flnomrk])/g);
        let styles = { color: defaultColor, obfuscated: false };

        segments.forEach(seg => {
            if (colorMap[seg]) {
                styles.color = colorMap[seg];
            } else if (formatMap[seg]) {
                if (seg === '&r') {
                    styles = { color: defaultColor, obfuscated: false };
                } else if (seg === '&k') {
                    styles.obfuscated = true;
                } else {
                    styles[formatMap[seg].name.toLowerCase()] = true;
                }
            } else {
                let displayText = seg;

                if (styles.obfuscated) {
                    displayText = `<span class="obfuscate" data-text="${seg}">${seg}</span>`;
                }

                let styleString = `color: ${styles.color}; ` +
                    `${styles.bold ? 'font-weight: bold;' : ''}` +
                    `${styles.underline ? 'text-decoration: underline;' : ''}` +
                    `${styles.italic ? 'font-style: italic;' : ''}` +
                    `${styles.strikethrough ? 'text-decoration: line-through;' : ''}`;

                outputHTML += `<span style="${styleString}">${displayText}</span>`;
            }
        });

        if (index < lines.length - 1) {
            outputHTML += '<br>';
        }
    });

    outputBox.innerHTML = outputHTML;
});

function obfuscateText(text) {
    return text.split('').map(char => getRandomChar()).join('');
}

function getRandomChar() {
    const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{};:,.<>?/|';
    return chars[Math.floor(Math.random() * chars.length)];
}

function updateObfuscatedText() {
    document.querySelectorAll('.obfuscate').forEach(span => {
        span.innerText = obfuscateText(span.dataset.text);
    });
}

setInterval(updateObfuscatedText, 50);

function setAlignment(align) {
    outputBox.style.textAlign = align;
}