// script.js

const API_BASE = 'http://localhost:8080/api';

const deviceNameInput = document.getElementById('deviceName');
const deviceTypeInput = document.getElementById('deviceType');
const deviceLocationInput = document.getElementById('deviceLocation');
const addDeviceBtn = document.getElementById('addDeviceBtn');
const devicesTableBody = document.getElementById('devicesTableBody');
const deviceMessageDiv = document.getElementById('deviceMessage');

const loanDeviceSelect = document.getElementById('loanDeviceSelect');
const loanBorrowedByInput = document.getElementById('loanBorrowedBy');
const addLoanBtn = document.getElementById('addLoanBtn');
const loansTableBody = document.getElementById('loansTableBody');
const loanMessageDiv = document.getElementById('loanMessage');

// Device Functions
async function fetchDevices() {
    deviceMessageDiv.textContent = '';
    deviceMessageDiv.className = '';
    try {
        const res = await fetch(`${API_BASE}/devices`);
        if (!res.ok) throw new Error('Failed to fetch devices');
        const devices = await res.json();
        renderDevices(devices);
        populateDeviceSelect(devices);
    } catch (err) {
        deviceMessageDiv.textContent = err.message;
        deviceMessageDiv.className = 'error';
    }
}

function renderDevices(devices) {
    devicesTableBody.innerHTML = '';
    for (const device of devices) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${device.id}</td>
            <td>${device.name}</td>
            <td>${device.type}</td>
            <td>${device.location}</td>
            <td>${device.status}</td>
            <td>
                <button onclick="deleteDevice(${device.id})">Delete</button>
            </td>
        `;
        devicesTableBody.appendChild(tr);
    }
}

function populateDeviceSelect(devices) {
    loanDeviceSelect.innerHTML = '';
    for (const device of devices) {
        if (device.status === 'AVAILABLE') {
            const option = document.createElement('option');
            option.value = device.id;
            option.textContent = `${device.name} (${device.type})`;
            loanDeviceSelect.appendChild(option);
        }
    }
}

async function addDevice() {
    deviceMessageDiv.textContent = '';
    deviceMessageDiv.className = '';
    const name = deviceNameInput.value.trim();
    const type = deviceTypeInput.value.trim();
    const location = deviceLocationInput.value.trim();

    if (!name || !type || !location) {
        deviceMessageDiv.textContent = 'Please fill in all device fields';
        deviceMessageDiv.className = 'error';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/devices`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, type, location }),
        });
        if (!res.ok) throw new Error('Failed to add device');
        deviceMessageDiv.textContent = 'Device added successfully';
        deviceMessageDiv.className = 'success';
        deviceNameInput.value = '';
        deviceTypeInput.value = '';
        deviceLocationInput.value = '';
        fetchDevices();
    } catch (err) {
        deviceMessageDiv.textContent = err.message;
        deviceMessageDiv.className = 'error';
    }
}

async function deleteDevice(id) {
    deviceMessageDiv.textContent = '';
    deviceMessageDiv.className = '';
    try {
        const res = await fetch(`${API_BASE}/devices/${id}`, { method: 'DELETE' });
        if (!res.ok) {
            const errText = await res.text();
            throw new Error(`Failed to delete device`);
        }
        deviceMessageDiv.textContent = 'Device deleted successfully';
        deviceMessageDiv.className = 'success';
        fetchDevices();
        fetchLoans();
    } catch (err) {
        deviceMessageDiv.textContent = err.message;
        deviceMessageDiv.className = 'error';
    }
}

// Loan Functions
async function fetchLoans() {
    loanMessageDiv.textContent = '';
    loanMessageDiv.className = '';
    try {
        const res = await fetch(`${API_BASE}/loans`);
        if (!res.ok) throw new Error('Failed to fetch loans');
        const loans = await res.json();
        renderLoans(loans);
    } catch (err) {
        loanMessageDiv.textContent = err.message;
        loanMessageDiv.className = 'error';
    }
}

function renderLoans(loans) {
    loansTableBody.innerHTML = '';
    for (const loan of loans) {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${loan.id}</td>
            <td>${loan.deviceId}</td>
            <td>${loan.borrowedBy}</td>
            <td>${new Date(loan.startDate).toLocaleString()}</td>
            <td>${loan.returned ? new Date(loan.endDate).toLocaleString() : 'Not returned'}</td>
            <td>
                ${loan.returned ? '' : `<button onclick="markReturned(${loan.id})">Mark Returned</button>`}
            </td>
        `;
        loansTableBody.appendChild(tr);
    }
}

async function addLoan() {
    loanMessageDiv.textContent = '';
    loanMessageDiv.className = '';
    const deviceId = parseInt(loanDeviceSelect.value);
    const borrowedBy = loanBorrowedByInput.value.trim();

    if (!deviceId || !borrowedBy) {
        loanMessageDiv.textContent = 'Please select a device and enter borrower name';
        loanMessageDiv.className = 'error';
        return;
    }

    try {
        const res = await fetch(`${API_BASE}/loans`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ deviceId, borrowedBy }),
        });
        if (!res.ok) {
            const errText = await res.text();
            throw new Error(`Failed to add loan: ${errText}`);
        }
        loanMessageDiv.textContent = 'Loan added successfully';
        loanMessageDiv.className = 'success';
        loanBorrowedByInput.value = '';
        fetchLoans();
        fetchDevices();
    } catch (err) {
        loanMessageDiv.textContent = err.message;
        loanMessageDiv.className = 'error';
    }
}

async function markReturned(id) {
    loanMessageDiv.textContent = '';
    loanMessageDiv.className = '';
    try {
        const res = await fetch(`${API_BASE}/loans/${id}/return`, { method: 'PUT' });
        if (!res.ok) {
            const errText = await res.text();
            throw new Error(`Failed to mark returned: ${errText}`);
        }
        loanMessageDiv.textContent = `Loan ${id} marked as returned`;
        loanMessageDiv.className = 'success';
        fetchLoans();
        fetchDevices();
    } catch (err) {
        loanMessageDiv.textContent = err.message;
        loanMessageDiv.className = 'error';
    }
}

// Event Listeners
addDeviceBtn.addEventListener('click', addDevice);
addLoanBtn.addEventListener('click', addLoan);

// Initial load
fetchDevices();
fetchLoans();
