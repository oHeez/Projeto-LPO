-- ============================================================
-- SCRIPT DE DADOS DE TESTE - SISTEMA PETSHOP
-- ============================================================
-- Este script insere dados de exemplo para todas as tabelas
-- do sistema petshop para facilitar testes e desenvolvimento.
-- 
-- Autor: Felipe
-- Data: 2024
-- ============================================================

USE db_petshop;

-- ============================================================
-- LIMPEZA DE DADOS EXISTENTES (OPCIONAL - DESCOMENTE SE NECESSÁRIO)
-- ============================================================
-- DELETE FROM TB_PET;
-- DELETE FROM TB_PRODUTO;
-- DELETE FROM TB_FUNCIONARIO;
-- DELETE FROM TB_CLIENTE;

-- ============================================================
-- INSERÇÃO DE CLIENTES
-- ============================================================
INSERT INTO TB_CLIENTE (nome, cpf, telefone, email, endereco, data_cadastro) VALUES
('João Silva', '12345678901', '81987654321', 'joao.silva@email.com', 'Rua das Flores, 123 - Recife/PE', NOW()),
('Maria Santos', '98765432100', '81912345678', 'maria.santos@email.com', 'Av. Boa Viagem, 456 - Recife/PE', NOW()),
('Pedro Oliveira', '11122233344', '81923456789', 'pedro.oliveira@email.com', 'Rua do Comércio, 789 - Olinda/PE', NOW()),
('Ana Costa', '55566677788', '81934567890', 'ana.costa@email.com', 'Rua da Praia, 321 - Jaboatão/PE', NOW()),
('Carlos Ferreira', '99988877766', '81945678901', 'carlos.ferreira@email.com', 'Av. Conde da Boa Vista, 654 - Recife/PE', NOW()),
('Juliana Alves', '44433322211', '81956789012', 'juliana.alves@email.com', 'Rua do Sol, 987 - Recife/PE', NOW()),
('Roberto Lima', '77788899900', '81967890123', 'roberto.lima@email.com', 'Rua da Paz, 147 - Recife/PE', NOW()),
('Fernanda Rocha', '22233344455', '81978901234', 'fernanda.rocha@email.com', 'Av. Agamenon Magalhães, 258 - Recife/PE', NOW());

-- ============================================================
-- INSERÇÃO DE FUNCIONÁRIOS
-- ============================================================

-- VETERINÁRIOS
INSERT INTO TB_FUNCIONARIO (nome, cpf, telefone, email, cargo, salario_base, data_contratacao, ativo, data_cadastro) VALUES
('Dr. Carlos Mendes', '11111111111', '81911111111', 'carlos.mendes@vet.com', 'VETERINARIO', 5000.00, '2020-01-15', TRUE, NOW()),
('Dra. Ana Paula', '22222222222', '81922222222', 'ana.paula@vet.com', 'VETERINARIO', 5500.00, '2019-03-20', TRUE, NOW()),
('Dr. Roberto Silva', '33333333333', '81933333333', 'roberto.silva@vet.com', 'VETERINARIO', 4800.00, '2021-06-10', TRUE, NOW());

-- TOSADORES
INSERT INTO TB_FUNCIONARIO (nome, cpf, telefone, email, cargo, salario_base, data_contratacao, ativo, data_cadastro) VALUES
('Marcos Tosa', '44444444444', '81944444444', 'marcos.tosa@pet.com', 'TOSADOR', 2500.00, '2020-05-12', TRUE, NOW()),
('Patricia Estética', '55555555555', '81955555555', 'patricia.estetica@pet.com', 'TOSADOR', 2700.00, '2019-08-25', TRUE, NOW()),
('Lucas Groomer', '66666666666', '81966666666', 'lucas.groomer@pet.com', 'TOSADOR', 2600.00, '2021-02-18', TRUE, NOW());

-- ATENDENTES
INSERT INTO TB_FUNCIONARIO (nome, cpf, telefone, email, cargo, salario_base, data_contratacao, ativo, data_cadastro) VALUES
('Juliana Atendimento', '77777777777', '81977777777', 'juliana.atendimento@pet.com', 'ATENDENTE', 1800.00, '2020-09-01', TRUE, NOW()),
('Ricardo Vendas', '88888888888', '81988888888', 'ricardo.vendas@pet.com', 'ATENDENTE', 1900.00, '2019-11-15', TRUE, NOW()),
('Amanda Recepção', '99999999999', '81999999999', 'amanda.recepcao@pet.com', 'ATENDENTE', 1850.00, '2021-04-03', TRUE, NOW());

-- ============================================================
-- INSERÇÃO DE PETS
-- ============================================================
-- Nota: Usa subconsultas para buscar o ID do cliente pelo CPF
-- Isso torna o script mais robusto e independente dos IDs gerados
-- Dividido em múltiplos INSERTs para compatibilidade com MariaDB

-- Pets do João Silva (CPF: 12345678901)
INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Rex', 'CÃO', 'Labrador', '2019-05-15', 28.5, 'Cão muito dócil e brincalhão', NOW() 
FROM TB_CLIENTE WHERE cpf = '12345678901';

INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Luna', 'GATO', 'Persa', '2020-03-20', 4.2, 'Gata calma, gosta de carinho', NOW() 
FROM TB_CLIENTE WHERE cpf = '12345678901';

-- Pets da Maria Santos (CPF: 98765432100)
INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Thor', 'CÃO', 'Golden Retriever', '2018-08-10', 32.0, 'Cão ativo, adora brincar', NOW() 
FROM TB_CLIENTE WHERE cpf = '98765432100';

INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Mel', 'CÃO', 'Shih Tzu', '2021-01-12', 6.5, 'Cão pequeno e carinhoso', NOW() 
FROM TB_CLIENTE WHERE cpf = '98765432100';

-- Pets do Pedro Oliveira (CPF: 11122233344)
INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Bolinha', 'GATO', 'Siamês', '2020-07-25', 3.8, 'Gato curioso e independente', NOW() 
FROM TB_CLIENTE WHERE cpf = '11122233344';

-- Pets da Ana Costa (CPF: 55566677788)
INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Max', 'CÃO', 'Bulldog', '2019-11-30', 22.0, 'Cão tranquilo, precisa de cuidados especiais', NOW() 
FROM TB_CLIENTE WHERE cpf = '55566677788';

INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Nina', 'GATO', 'Maine Coon', '2019-04-18', 5.5, 'Gata grande e peluda', NOW() 
FROM TB_CLIENTE WHERE cpf = '55566677788';

-- Pets do Carlos Ferreira (CPF: 99988877766)
INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Zeus', 'CÃO', 'Pastor Alemão', '2018-12-05', 35.0, 'Cão grande e protetor', NOW() 
FROM TB_CLIENTE WHERE cpf = '99988877766';

-- Pets da Juliana Alves (CPF: 44433322211)
INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Mimi', 'GATO', 'Angorá', '2021-02-14', 3.5, 'Gata branca e fofa', NOW() 
FROM TB_CLIENTE WHERE cpf = '44433322211';

INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Toby', 'CÃO', 'Poodle', '2020-09-08', 8.0, 'Cão inteligente e brincalhão', NOW() 
FROM TB_CLIENTE WHERE cpf = '44433322211';

-- Pets do Roberto Lima (CPF: 77788899900)
INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Bella', 'CÃO', 'Yorkshire', '2020-06-22', 3.2, 'Cão pequeno e energético', NOW() 
FROM TB_CLIENTE WHERE cpf = '77788899900';

-- Pets da Fernanda Rocha (CPF: 22233344455)
INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Jack', 'CÃO', 'Husky Siberiano', '2019-10-15', 25.0, 'Cão ativo, precisa de exercícios', NOW() 
FROM TB_CLIENTE WHERE cpf = '22233344455';

INSERT INTO TB_PET (id_cliente, nome, especie, raca, data_nascimento, peso, observacoes, data_cadastro) 
SELECT id_cliente, 'Lily', 'GATO', 'Ragdoll', '2020-12-01', 4.8, 'Gata dócil e tranquila', NOW() 
FROM TB_CLIENTE WHERE cpf = '22233344455';

-- ============================================================
-- INSERÇÃO DE PRODUTOS
-- ============================================================

-- RAÇÃO
INSERT INTO TB_PRODUTO (nome, descricao, preco, estoque, categoria, ativo, data_cadastro) VALUES
('Ração Premium Cães Adultos 15kg', 'Ração completa e balanceada para cães adultos de todas as raças', 189.90, 45, 'RACAO', TRUE, NOW()),
('Ração Premium Gatos Adultos 10kg', 'Ração completa para gatos adultos, sabor peixe', 159.90, 38, 'RACAO', TRUE, NOW()),
('Ração Filhotes 12kg', 'Ração especial para filhotes de cães até 12 meses', 149.90, 25, 'RACAO', TRUE, NOW()),
('Ração Light Cães 15kg', 'Ração para cães com tendência à obesidade', 199.90, 20, 'RACAO', TRUE, NOW()),
('Ração Premium Gatos Castrados 10kg', 'Ração especial para gatos castrados', 169.90, 30, 'RACAO', TRUE, NOW());

-- REMÉDIOS
INSERT INTO TB_PRODUTO (nome, descricao, preco, estoque, categoria, ativo, data_cadastro) VALUES
('Vermífugo para Cães', 'Vermífugo de amplo espectro para cães de todas as idades', 45.90, 60, 'REMEDIO', TRUE, NOW()),
('Vermífugo para Gatos', 'Vermífugo de amplo espectro para gatos de todas as idades', 42.90, 55, 'REMEDIO', TRUE, NOW()),
('Antipulgas e Carrapatos 4 doses', 'Prevenção contra pulgas e carrapatos, 4 doses', 89.90, 40, 'REMEDIO', TRUE, NOW()),
('Shampoo Antipulgas', 'Shampoo para banho com ação antipulgas', 35.90, 35, 'REMEDIO', TRUE, NOW()),
('Suplemento Vitamínico', 'Complexo vitamínico para cães e gatos', 58.90, 50, 'REMEDIO', TRUE, NOW());

-- BRINQUEDOS
INSERT INTO TB_PRODUTO (nome, descricao, preco, estoque, categoria, ativo, data_cadastro) VALUES
('Bola Interativa', 'Bola com som para entreter cães', 24.90, 80, 'BRINQUEDO', TRUE, NOW()),
('Arranhador para Gatos', 'Arranhador vertical com plataformas', 129.90, 15, 'BRINQUEDO', TRUE, NOW()),
('Osso de Nylon', 'Osso resistente para cães roerem', 18.90, 100, 'BRINQUEDO', TRUE, NOW()),
('Ratinho de Pelúcia', 'Brinquedo de pelúcia para gatos', 12.90, 120, 'BRINQUEDO', TRUE, NOW()),
('Corda para Cães', 'Corda resistente para brincadeiras', 15.90, 90, 'BRINQUEDO', TRUE, NOW());

-- ACESSÓRIOS
INSERT INTO TB_PRODUTO (nome, descricao, preco, estoque, categoria, ativo, data_cadastro) VALUES
('Coleira Ajustável', 'Coleira de nylon ajustável com fivela', 29.90, 70, 'ACESSORIO', TRUE, NOW()),
('Guia Retrátil 5m', 'Guia retrátil de 5 metros para passeios', 79.90, 25, 'ACESSORIO', TRUE, NOW()),
('Comedouro Inox Duplo', 'Comedouro duplo em aço inoxidável', 45.90, 40, 'ACESSORIO', TRUE, NOW()),
('Cama para Cães M', 'Cama confortável tamanho médio', 89.90, 20, 'ACESSORIO', TRUE, NOW()),
('Caixa de Areia para Gatos', 'Caixa de areia com tampa e peneira', 65.90, 18, 'ACESSORIO', TRUE, NOW()),
('Roupinha para Cães P', 'Roupinha para cães pequenos', 39.90, 30, 'ACESSORIO', TRUE, NOW());

-- ============================================================
-- VERIFICAÇÃO DOS DADOS INSERIDOS
-- ============================================================

-- Contagem de registros inseridos
SELECT 'CLIENTES' AS Tabela, COUNT(*) AS Total FROM TB_CLIENTE
UNION ALL
SELECT 'FUNCIONARIOS' AS Tabela, COUNT(*) AS Total FROM TB_FUNCIONARIO
UNION ALL
SELECT 'PETS' AS Tabela, COUNT(*) AS Total FROM TB_PET
UNION ALL
SELECT 'PRODUTOS' AS Tabela, COUNT(*) AS Total FROM TB_PRODUTO;

-- Listagem de funcionários por cargo
SELECT cargo, COUNT(*) AS quantidade, 
       CONCAT('R$ ', FORMAT(AVG(salario_base), 2, 'de_DE')) AS salario_medio
FROM TB_FUNCIONARIO 
GROUP BY cargo
ORDER BY cargo;

-- Listagem de produtos por categoria
SELECT categoria, COUNT(*) AS quantidade,
       CONCAT('R$ ', FORMAT(AVG(preco), 2, 'de_DE')) AS preco_medio,
       SUM(estoque) AS estoque_total
FROM TB_PRODUTO
GROUP BY categoria
ORDER BY categoria;

-- ============================================================
-- FIM DO SCRIPT
-- ============================================================

