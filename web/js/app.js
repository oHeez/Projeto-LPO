// ============================================
// GERENCIAMENTO DE TABS
// ============================================

document.addEventListener('DOMContentLoaded', () => {
    // Inicializar tabs
    document.querySelectorAll('.tab-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            const tabName = btn.getAttribute('data-tab');
            switchTab(tabName);
        });
    });

    // Carregar dados iniciais
    loadClientes();
    loadFuncionarios();
    loadPets();
    loadProdutos();
});

function switchTab(tabName) {
    // Desativar todas as tabs
    document.querySelectorAll('.tab-btn').forEach(btn => btn.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(content => content.classList.remove('active'));

    // Ativar tab selecionada
    document.querySelector(`[data-tab="${tabName}"]`).classList.add('active');
    document.getElementById(`tab-${tabName}`).classList.add('active');
}

// ============================================
// UTILITARIOS
// ============================================

async function fetchAPI(endpoint, method = 'GET', data = null) {
    try {
        const options = {
            method: method,
            headers: {
                'Content-Type': 'application/json'
            }
        };

        if (data && method !== 'GET') {
            options.body = JSON.stringify(data);
        }

        const response = await fetch(endpoint, options);
        
        let result;
        const contentType = response.headers.get('content-type');
        if (contentType && contentType.includes('application/json')) {
            result = await response.json();
        } else {
            const text = await response.text();
            try {
                result = text ? JSON.parse(text) : { mensagem: 'Operacao realizada com sucesso' };
            } catch (e) {
                result = { mensagem: text || 'Operacao realizada com sucesso' };
            }
        }
        
        if (!response.ok) {
            throw new Error(result.erro || result.mensagem || `Erro ${response.status}: ${response.statusText}`);
        }

        // Se result já é um array, retorna diretamente
        // Se result tem propriedade 'data', usa ela
        // Caso contrário, retorna o próprio result
        let responseData;
        if (Array.isArray(result)) {
            responseData = result;
        } else if (result && typeof result === 'object' && result.data !== undefined) {
            responseData = result.data;
        } else {
            responseData = result;
        }
        
        return { success: true, data: responseData };
    } catch (error) {
        console.error('Erro na requisicao:', error);
        console.error('Endpoint:', endpoint);
        console.error('Method:', method);
        console.error('Data:', data);
        
        let errorMessage = error.message;
        if (error.message.includes('Failed to fetch') || error.message.includes('NetworkError')) {
            errorMessage = 'Erro de conexao: Verifique se o servidor esta rodando em http://localhost:8080';
        }
        
        return { success: false, error: errorMessage };
    }
}

function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// ============================================
// CLIENTES
// ============================================

async function loadClientes() {
    const result = await fetchAPI('/api/clientes');
    const tbody = document.getElementById('tbody-clientes');
    
    console.log('Resultado loadClientes:', result);
    
    if (!result.success) {
        const errorMsg = result.error || 'Erro desconhecido ao carregar clientes';
        console.error('Erro ao carregar clientes:', errorMsg);
        tbody.innerHTML = `<tr><td colspan="6" class="loading">${errorMsg}</td></tr>`;
        return;
    }

    // Garantir que result.data é um array
    let clientes = [];
    if (Array.isArray(result.data)) {
        clientes = result.data;
    } else if (result.data && Array.isArray(result.data.data)) {
        clientes = result.data.data;
    } else if (result.data && typeof result.data === 'object') {
        // Se for um objeto único, transforma em array
        clientes = [result.data];
    }
    
    if (clientes.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Nenhum cliente cadastrado</td></tr>';
        return;
    }

    tbody.innerHTML = clientes.map(cliente => {
        const id = cliente.idCliente || cliente.id || '-';
        const nome = cliente.nome || '-';
        const cpf = cliente.cpf || '-';
        const telefone = cliente.telefone || '-';
        const email = cliente.email || '-';
        
        return `
        <tr>
            <td>${id}</td>
            <td>${nome}</td>
            <td>${cpf}</td>
            <td>${telefone}</td>
            <td>${email}</td>
            <td>
                <button class="btn-edit" onclick="editCliente(${id})">Editar</button>
                <button class="btn-delete" onclick="deleteCliente(${id})">Excluir</button>
            </td>
        </tr>
        `;
    }).join('');
}

function openClienteModal(cliente = null) {
    const modal = document.getElementById('modal-cliente');
    const form = document.getElementById('form-cliente');
    const title = document.getElementById('modal-cliente-title');

    if (cliente) {
        title.textContent = 'Editar Cliente';
        document.getElementById('cliente-id').value = cliente.idCliente;
        document.getElementById('cliente-nome').value = cliente.nome || '';
        document.getElementById('cliente-cpf').value = cliente.cpf || '';
        document.getElementById('cliente-telefone').value = cliente.telefone || '';
        document.getElementById('cliente-email').value = cliente.email || '';
        document.getElementById('cliente-endereco').value = cliente.endereco || '';
    } else {
        title.textContent = 'Novo Cliente';
        form.reset();
        document.getElementById('cliente-id').value = '';
    }

    modal.classList.add('active');
}

function closeClienteModal() {
    document.getElementById('modal-cliente').classList.remove('active');
}

async function saveCliente(event) {
    event.preventDefault();
    
    const id = document.getElementById('cliente-id').value;
    const cliente = {
        nome: document.getElementById('cliente-nome').value,
        cpf: document.getElementById('cliente-cpf').value,
        telefone: document.getElementById('cliente-telefone').value || null,
        email: document.getElementById('cliente-email').value || null,
        endereco: document.getElementById('cliente-endereco').value || null
    };

    const endpoint = id ? `/api/clientes/${id}` : '/api/clientes';
    const method = id ? 'PUT' : 'POST';
    
    const result = await fetchAPI(endpoint, method, cliente);
    
    if (result.success) {
        showToast(id ? 'Cliente atualizado com sucesso!' : 'Cliente cadastrado com sucesso!');
        closeClienteModal();
        loadClientes();
    } else {
        showToast(result.error, 'error');
    }
}

async function editCliente(id) {
    const result = await fetchAPI(`/api/clientes/${id}`);
    
    if (result.success) {
        openClienteModal(result.data);
    } else {
        showToast(result.error, 'error');
    }
}

async function deleteCliente(id) {
    if (!confirm('Tem certeza que deseja excluir este cliente?')) {
        return;
    }

    const result = await fetchAPI(`/api/clientes/${id}`, 'DELETE');
    
    if (result.success) {
        showToast(result.data.mensagem || 'Cliente excluido com sucesso!');
        loadClientes();
    } else {
        showToast(result.error, 'error');
    }
}

// ============================================
// FUNCIONARIOS
// ============================================

async function loadFuncionarios() {
    const result = await fetchAPI('/api/funcionarios');
    const tbody = document.getElementById('tbody-funcionarios');
    
    if (!result.success) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Erro ao carregar funcionarios</td></tr>';
        return;
    }

    if (result.data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Nenhum funcionario cadastrado</td></tr>';
        return;
    }

    tbody.innerHTML = result.data.map(func => `
        <tr>
            <td>${func.idFuncionario}</td>
            <td>${func.nome}</td>
            <td>${func.cpf || '-'}</td>
            <td>${func.cargo || '-'}</td>
            <td>R$ ${(func.salarioBase || 0).toFixed(2)}</td>
            <td>
                <button class="btn-edit" onclick="editFuncionario(${func.idFuncionario})">Editar</button>
                <button class="btn-delete" onclick="deleteFuncionario(${func.idFuncionario})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

function openFuncionarioModal(funcionario = null) {
    const modal = document.getElementById('modal-funcionario');
    const form = document.getElementById('form-funcionario');
    const title = document.getElementById('modal-funcionario-title');

    if (funcionario) {
        title.textContent = 'Editar Funcionario';
        document.getElementById('funcionario-id').value = funcionario.idFuncionario;
        document.getElementById('funcionario-nome').value = funcionario.nome || '';
        document.getElementById('funcionario-cpf').value = funcionario.cpf || '';
        document.getElementById('funcionario-telefone').value = funcionario.telefone || '';
        document.getElementById('funcionario-email').value = funcionario.email || '';
        document.getElementById('funcionario-cargo').value = funcionario.cargo || '';
        document.getElementById('funcionario-salario').value = funcionario.salarioBase || '';
        
        if (funcionario.dataContratacao) {
            const date = new Date(funcionario.dataContratacao.split('T')[0]);
            document.getElementById('funcionario-data').value = date.toISOString().split('T')[0];
        }
    } else {
        title.textContent = 'Novo Funcionario';
        form.reset();
        document.getElementById('funcionario-id').value = '';
    }

    modal.classList.add('active');
}

function closeFuncionarioModal() {
    document.getElementById('modal-funcionario').classList.remove('active');
}

async function saveFuncionario(event) {
    event.preventDefault();
    
    const id = document.getElementById('funcionario-id').value;
    const funcionario = {
        nome: document.getElementById('funcionario-nome').value,
        cpf: document.getElementById('funcionario-cpf').value,
        telefone: document.getElementById('funcionario-telefone').value || null,
        email: document.getElementById('funcionario-email').value || null,
        cargo: document.getElementById('funcionario-cargo').value,
        salarioBase: document.getElementById('funcionario-salario').value ? parseFloat(document.getElementById('funcionario-salario').value) : null,
        dataContratacao: document.getElementById('funcionario-data').value || null
    };

    const endpoint = id ? `/api/funcionarios/${id}` : '/api/funcionarios';
    const method = id ? 'PUT' : 'POST';
    
    const result = await fetchAPI(endpoint, method, funcionario);
    
    if (result.success) {
        showToast(id ? 'Funcionario atualizado com sucesso!' : 'Funcionario cadastrado com sucesso!');
        closeFuncionarioModal();
        loadFuncionarios();
    } else {
        showToast(result.error, 'error');
    }
}

async function editFuncionario(id) {
    const result = await fetchAPI(`/api/funcionarios/${id}`);
    
    if (result.success) {
        openFuncionarioModal(result.data);
    } else {
        showToast(result.error, 'error');
    }
}

async function deleteFuncionario(id) {
    if (!confirm('Tem certeza que deseja excluir este funcionario?')) {
        return;
    }

    const result = await fetchAPI(`/api/funcionarios/${id}`, 'DELETE');
    
    if (result.success) {
        showToast(result.data.mensagem || 'Funcionario excluido com sucesso!');
        loadFuncionarios();
    } else {
        showToast(result.error, 'error');
    }
}

// ============================================
// PETS
// ============================================

async function loadPets() {
    const result = await fetchAPI('/api/pets');
    const tbody = document.getElementById('tbody-pets');
    
    if (!result.success) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Erro ao carregar pets</td></tr>';
        return;
    }

    if (result.data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Nenhum pet cadastrado</td></tr>';
        return;
    }

    tbody.innerHTML = result.data.map(pet => `
        <tr>
            <td>${pet.idPet}</td>
            <td>${pet.nome}</td>
            <td>${pet.especie || '-'}</td>
            <td>${pet.raca || '-'}</td>
            <td>${pet.idCliente}</td>
            <td>
                <button class="btn-edit" onclick="editPet(${pet.idPet})">Editar</button>
                <button class="btn-delete" onclick="deletePet(${pet.idPet})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

function openPetModal(pet = null) {
    const modal = document.getElementById('modal-pet');
    const form = document.getElementById('form-pet');
    const title = document.getElementById('modal-pet-title');

    if (pet) {
        title.textContent = 'Editar Pet';
        document.getElementById('pet-id').value = pet.idPet;
        document.getElementById('pet-id-cliente').value = pet.idCliente || '';
        document.getElementById('pet-nome').value = pet.nome || '';
        document.getElementById('pet-especie').value = pet.especie || '';
        document.getElementById('pet-raca').value = pet.raca || '';
        document.getElementById('pet-peso').value = pet.peso || '';
        document.getElementById('pet-observacoes').value = pet.observacoes || '';
        
        if (pet.dataNascimento) {
            const date = new Date(pet.dataNascimento.split('T')[0]);
            document.getElementById('pet-data-nascimento').value = date.toISOString().split('T')[0];
        }
    } else {
        title.textContent = 'Novo Pet';
        form.reset();
        document.getElementById('pet-id').value = '';
    }

    modal.classList.add('active');
}

function closePetModal() {
    document.getElementById('modal-pet').classList.remove('active');
}

async function savePet(event) {
    event.preventDefault();
    
    const id = document.getElementById('pet-id').value;
    const pet = {
        idCliente: parseInt(document.getElementById('pet-id-cliente').value),
        nome: document.getElementById('pet-nome').value,
        especie: document.getElementById('pet-especie').value,
        raca: document.getElementById('pet-raca').value || null,
        dataNascimento: document.getElementById('pet-data-nascimento').value || null,
        peso: document.getElementById('pet-peso').value ? parseFloat(document.getElementById('pet-peso').value) : null,
        observacoes: document.getElementById('pet-observacoes').value || null
    };

    const endpoint = id ? `/api/pets/${id}` : '/api/pets';
    const method = id ? 'PUT' : 'POST';
    
    const result = await fetchAPI(endpoint, method, pet);
    
    if (result.success) {
        showToast(id ? 'Pet atualizado com sucesso!' : 'Pet cadastrado com sucesso!');
        closePetModal();
        loadPets();
    } else {
        showToast(result.error, 'error');
    }
}

async function editPet(id) {
    const result = await fetchAPI(`/api/pets/${id}`);
    
    if (result.success) {
        openPetModal(result.data);
    } else {
        showToast(result.error, 'error');
    }
}

async function deletePet(id) {
    if (!confirm('Tem certeza que deseja excluir este pet?')) {
        return;
    }

    const result = await fetchAPI(`/api/pets/${id}`, 'DELETE');
    
    if (result.success) {
        showToast(result.data.mensagem || 'Pet excluido com sucesso!');
        loadPets();
    } else {
        showToast(result.error, 'error');
    }
}

// ============================================
// PRODUTOS
// ============================================

async function loadProdutos() {
    const result = await fetchAPI('/api/produtos');
    const tbody = document.getElementById('tbody-produtos');
    
    if (!result.success) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Erro ao carregar produtos</td></tr>';
        return;
    }

    if (result.data.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="loading">Nenhum produto cadastrado</td></tr>';
        return;
    }

    tbody.innerHTML = result.data.map(produto => `
        <tr>
            <td>${produto.idProduto}</td>
            <td>${produto.nome}</td>
            <td>${produto.categoria || '-'}</td>
            <td>R$ ${(produto.preco || 0).toFixed(2)}</td>
            <td>${produto.estoque || 0}</td>
            <td>
                <button class="btn-edit" onclick="editProduto(${produto.idProduto})">Editar</button>
                <button class="btn-delete" onclick="deleteProduto(${produto.idProduto})">Excluir</button>
            </td>
        </tr>
    `).join('');
}

function openProdutoModal(produto = null) {
    const modal = document.getElementById('modal-produto');
    const form = document.getElementById('form-produto');
    const title = document.getElementById('modal-produto-title');

    if (produto) {
        title.textContent = 'Editar Produto';
        document.getElementById('produto-id').value = produto.idProduto;
        document.getElementById('produto-nome').value = produto.nome || '';
        document.getElementById('produto-descricao').value = produto.descricao || '';
        document.getElementById('produto-preco').value = produto.preco || '';
        document.getElementById('produto-estoque').value = produto.estoque || 0;
        document.getElementById('produto-categoria').value = produto.categoria || '';
    } else {
        title.textContent = 'Novo Produto';
        form.reset();
        document.getElementById('produto-id').value = '';
        document.getElementById('produto-estoque').value = 0;
    }

    modal.classList.add('active');
}

function closeProdutoModal() {
    document.getElementById('modal-produto').classList.remove('active');
}

async function saveProduto(event) {
    event.preventDefault();
    
    const id = document.getElementById('produto-id').value;
    const produto = {
        nome: document.getElementById('produto-nome').value,
        descricao: document.getElementById('produto-descricao').value || null,
        preco: parseFloat(document.getElementById('produto-preco').value),
        estoque: parseInt(document.getElementById('produto-estoque').value) || 0,
        categoria: document.getElementById('produto-categoria').value
    };

    const endpoint = id ? `/api/produtos/${id}` : '/api/produtos';
    const method = id ? 'PUT' : 'POST';
    
    const result = await fetchAPI(endpoint, method, produto);
    
    if (result.success) {
        showToast(id ? 'Produto atualizado com sucesso!' : 'Produto cadastrado com sucesso!');
        closeProdutoModal();
        loadProdutos();
    } else {
        showToast(result.error, 'error');
    }
}

async function editProduto(id) {
    const result = await fetchAPI(`/api/produtos/${id}`);
    
    if (result.success) {
        openProdutoModal(result.data);
    } else {
        showToast(result.error, 'error');
    }
}

async function deleteProduto(id) {
    if (!confirm('Tem certeza que deseja excluir este produto?')) {
        return;
    }

    const result = await fetchAPI(`/api/produtos/${id}`, 'DELETE');
    
    if (result.success) {
        showToast(result.data.mensagem || 'Produto excluido com sucesso!');
        loadProdutos();
    } else {
        showToast(result.error, 'error');
    }
}

