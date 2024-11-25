package SistemaHotelEstrutura2;

import java.util.*;

public class GerenciadorReservas {
	private enum Cor { VERMELHO, PRETO }

	private class Nodo {
        Reserva reserva;
        Cor cor;
        Nodo esquerdo, direito, pai;

        public Nodo(Reserva reserva) {
            this.reserva = reserva;
            this.cor = Cor.VERMELHO;
            esquerdo = direito = pai = null;
        }

        public boolean isFilhoEsquerdo() {
            return pai != null && pai.esquerdo == this;
        }
	}

	private Nodo raiz;
    private Map<String, List<Reserva>> reservasPorCPF;
    private static int nextId = 1;

    public GerenciadorReservas() {
        raiz = null;
        reservasPorCPF = new HashMap<>();
    }

    public void inserir(Reserva reserva) {
        if (!verificarDisponibilidade(reserva.getQuarto().getNumero(), 
                                    reserva.getCheckIn(), 
                                    reserva.getCheckOut())) {
            throw new IllegalStateException("Quarto não disponível para o período solicitado");
        }

        String cpf = reserva.getUsuario().getCPF();
        reservasPorCPF.computeIfAbsent(cpf, k -> new ArrayList<>()).add(reserva);

        Nodo novoNodo = new Nodo(reserva);
        if (raiz == null) {
            raiz = novoNodo;
            novoNodo.cor = Cor.PRETO;
        } else {
            inserirNodoNaArvore(novoNodo);
        }
    }
    
    private void inserirNodoNaArvore(Nodo novo) {
        Nodo pai = null;
        Nodo atual = raiz;

        // Encontrar posição de inserção
        while (atual != null) {
            pai = atual;
            if (novo.reserva.getIdReserva().compareTo(atual.reserva.getIdReserva()) < 0) {
                atual = atual.esquerdo;
            } else {
                atual = atual.direito;
            }
        }

        novo.pai = pai;
        if (pai == null) {
            raiz = novo;
        } else if (novo.reserva.getIdReserva().compareTo(pai.reserva.getIdReserva()) < 0) {
            pai.esquerdo = novo;
        } else {
            pai.direito = novo;
        }

        corrigirInsercao(novo);
    }

    private void corrigirInsercao(Nodo nodo) {
        while (nodo != raiz && nodo.pai.cor == Cor.VERMELHO) {
            if (nodo.pai == nodo.pai.pai.esquerdo) {
                Nodo tio = nodo.pai.pai.direito;
                
                if (tio != null && tio.cor == Cor.VERMELHO) {
                    // Caso 1: Tio é vermelho
                    nodo.pai.cor = Cor.PRETO;
                    tio.cor = Cor.PRETO;
                    nodo.pai.pai.cor = Cor.VERMELHO;
                    nodo = nodo.pai.pai;
                } else {
                    // Caso 2: Tio é preto e nodo é filho direito
                    if (nodo == nodo.pai.direito) {
                        nodo = nodo.pai;
                        rotacaoEsquerda(nodo);
                    }
                    // Caso 3: Tio é preto e nodo é filho esquerdo
                    nodo.pai.cor = Cor.PRETO;
                    nodo.pai.pai.cor = Cor.VERMELHO;
                    rotacaoDireita(nodo.pai.pai);
                }
            } else {
                // Mesma lógica do if, mas espelhada
                Nodo tio = nodo.pai.pai.esquerdo;
                
                if (tio != null && tio.cor == Cor.VERMELHO) {
                    nodo.pai.cor = Cor.PRETO;
                    tio.cor = Cor.PRETO;
                    nodo.pai.pai.cor = Cor.VERMELHO;
                    nodo = nodo.pai.pai;
                } else {
                    if (nodo == nodo.pai.esquerdo) {
                        nodo = nodo.pai;
                        rotacaoDireita(nodo);
                    }
                    nodo.pai.cor = Cor.PRETO;
                    nodo.pai.pai.cor = Cor.VERMELHO;
                    rotacaoEsquerda(nodo.pai.pai);
                }
            }
        }
        raiz.cor = Cor.PRETO;
    }
    public void remover(String idReserva) {
        Nodo nodo = buscarNodo(raiz, idReserva);
        if (nodo != null) {
            removerNodo(nodo);
        }
    }

    private Nodo buscarNodo(Nodo nodo, String idReserva) {
        if (nodo == null || idReserva.equals(nodo.reserva.getIdReserva())) {
            return nodo;
        }
        if (idReserva.compareTo(nodo.reserva.getIdReserva()) < 0) {
            return buscarNodo(nodo.esquerdo, idReserva);
        }
        return buscarNodo(nodo.direito, idReserva);
    }

    private void removerNodo(Nodo nodo) {
        Nodo sucessor = nodo;
        Nodo filho;
        Cor corOriginal = sucessor.cor;

        if (nodo.esquerdo == null) {
            filho = nodo.direito;
            transplante(nodo, nodo.direito);
        } else if (nodo.direito == null) {
            filho = nodo.esquerdo;
            transplante(nodo, nodo.esquerdo);
        } else {
            sucessor = encontrarMinimo(nodo.direito);
            corOriginal = sucessor.cor;
            filho = sucessor.direito;
            
            if (sucessor.pai == nodo) {
                if (filho != null) {
                    filho.pai = sucessor;
                }
            } else {
                transplante(sucessor, sucessor.direito);
                sucessor.direito = nodo.direito;
                sucessor.direito.pai = sucessor;
            }
            
            transplante(nodo, sucessor);
            sucessor.esquerdo = nodo.esquerdo;
            sucessor.esquerdo.pai = sucessor;
            sucessor.cor = nodo.cor;
        }

        if (corOriginal == Cor.PRETO && filho != null) {
            corrigirRemocao(filho);
        }
    }


    private void transplante(Nodo u, Nodo v) {
        if (u.pai == null) {
            raiz = v;
        } else if (u.isFilhoEsquerdo()) {
            u.pai.esquerdo = v;
        } else {
            u.pai.direito = v;
        }
        if (v != null) {
            v.pai = u.pai;
        }
    }

    private Nodo encontrarMinimo(Nodo nodo) {
        while (nodo.esquerdo != null) {
            nodo = nodo.esquerdo;
        }
        return nodo;
    }

    public Reserva buscarReservaPorCliente(String cpf) {
        List<Reserva> reservasDoCliente = reservasPorCPF.get(cpf);
        if (reservasDoCliente != null && !reservasDoCliente.isEmpty()) {
            return reservasDoCliente.stream()
                .filter(r -> r.getStatus() != Reserva.StatusReserva.CANCELADA)
                .max(Comparator.comparing(Reserva::getCheckIn))
                .orElse(null);
        }
        return null;
    }

    private void corrigirRemocao(Nodo nodo) {
        while (nodo != raiz && nodo.cor == Cor.PRETO) {
            if (nodo == nodo.pai.esquerdo) {
                Nodo irmao = nodo.pai.direito;
                
                if (irmao.cor == Cor.VERMELHO) {
                    irmao.cor = Cor.PRETO;
                    nodo.pai.cor = Cor.VERMELHO;
                    rotacaoEsquerda(nodo.pai);
                    irmao = nodo.pai.direito;
                }

                if ((irmao.esquerdo == null || irmao.esquerdo.cor == Cor.PRETO) &&
                    (irmao.direito == null || irmao.direito.cor == Cor.PRETO)) {
                    irmao.cor = Cor.VERMELHO;
                    nodo = nodo.pai;
                } else {
                    if (irmao.direito == null || irmao.direito.cor == Cor.PRETO) {
                        if (irmao.esquerdo != null) {
                            irmao.esquerdo.cor = Cor.PRETO;
                        }
                        irmao.cor = Cor.VERMELHO;
                        rotacaoDireita(irmao);
                        irmao = nodo.pai.direito;
                    }
                    irmao.cor = nodo.pai.cor;
                    nodo.pai.cor = Cor.PRETO;
                    if (irmao.direito != null) {
                        irmao.direito.cor = Cor.PRETO;
                    }
                    rotacaoEsquerda(nodo.pai);
                    nodo = raiz;
                }
            } else {
                // Mesma lógica do if, mas espelhada
                Nodo irmao = nodo.pai.esquerdo;
                
                if (irmao.cor == Cor.VERMELHO) {
                    irmao.cor = Cor.PRETO;
                    nodo.pai.cor = Cor.VERMELHO;
                    rotacaoDireita(nodo.pai);
                    irmao = nodo.pai.esquerdo;
                }

                if ((irmao.direito == null || irmao.direito.cor == Cor.PRETO) &&
                    (irmao.esquerdo == null || irmao.esquerdo.cor == Cor.PRETO)) {
                    irmao.cor = Cor.VERMELHO;
                    nodo = nodo.pai;
                } else {
                    if (irmao.esquerdo == null || irmao.esquerdo.cor == Cor.PRETO) {
                        if (irmao.direito != null) {
                            irmao.direito.cor = Cor.PRETO;
                        }
                        irmao.cor = Cor.VERMELHO;
                        rotacaoEsquerda(irmao);
                        irmao = nodo.pai.esquerdo;
                    }
                    irmao.cor = nodo.pai.cor;
                    nodo.pai.cor = Cor.PRETO;
                    if (irmao.esquerdo != null) {
                        irmao.esquerdo.cor = Cor.PRETO;
                    }
                    rotacaoDireita(nodo.pai);
                    nodo = raiz;
                }
            }
        }
        nodo.cor = Cor.PRETO;
    }

    public List<Reserva> listarReservasPorData(Date data) {
        List<Reserva> reservasDaData = new ArrayList<>();
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(data);
        
        for (Reserva reserva : listarReservasPorData()) {
            if (reserva.getStatus() == Reserva.StatusReserva.CANCELADA) {
                continue;
            }
            
            cal2.setTime(reserva.getCheckIn());
            if (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH)) {
                reservasDaData.add(reserva);
            }
        }
        return reservasDaData;
    }

    public List<Reserva> listarReservasAtivas() {
        List<Reserva> reservasAtivas = new ArrayList<>();
        percorrerEmOrdemAtivas(raiz, reservasAtivas);
        return reservasAtivas;
    }
    private void percorrerEmOrdemAtivas(Nodo nodo, List<Reserva> reservas) {
        if (nodo != null) {
            percorrerEmOrdemAtivas(nodo.esquerdo, reservas);
            if (nodo.reserva.getStatus() != Reserva.StatusReserva.CANCELADA) {
                reservas.add(nodo.reserva);
            }
            percorrerEmOrdemAtivas(nodo.direito, reservas);
        }
    }

    public List<Reserva> listarReservasPorData() {
        List<Reserva> reservasOrdenadas = new ArrayList<>();
        percorrerEmOrdem(raiz, reservasOrdenadas);
        return reservasOrdenadas;
    }

    private void percorrerEmOrdem(Nodo nodo, List<Reserva> reservas) {
        if (nodo != null) {
            percorrerEmOrdem(nodo.esquerdo, reservas);
            reservas.add(nodo.reserva);
            percorrerEmOrdem(nodo.direito, reservas);
        }
    }

    public boolean verificarDisponibilidade(int numeroQuarto, Date checkIn, Date checkOut) {
        return verificarDisponibilidadeRecursivo(raiz, numeroQuarto, checkIn, checkOut);
    }

    private boolean verificarDisponibilidadeRecursivo(Nodo nodo, int numeroQuarto, 
                                                     Date checkIn, Date checkOut) {
        if (nodo == null) return true;

        if (nodo.reserva.getQuarto().getNumero() == numeroQuarto) {
            if (nodo.reserva.getStatus() != Reserva.StatusReserva.CANCELADA &&
                checkIn.before(nodo.reserva.getCheckOut()) && 
                checkOut.after(nodo.reserva.getCheckIn())) {
                return false;
            }
        }

        return verificarDisponibilidadeRecursivo(nodo.esquerdo, numeroQuarto, checkIn, checkOut) &&
               verificarDisponibilidadeRecursivo(nodo.direito, numeroQuarto, checkIn, checkOut);
    }

    private void rotacaoEsquerda(Nodo nodo) {
        Nodo direito = nodo.direito;
        nodo.direito = direito.esquerdo;
        
        if (direito.esquerdo != null) {
            direito.esquerdo.pai = nodo;
        }
        
        direito.pai = nodo.pai;
        
        if (nodo.pai == null) {
            raiz = direito;
        } else if (nodo == nodo.pai.esquerdo) {
            nodo.pai.esquerdo = direito;
        } else {
            nodo.pai.direito = direito;
        }
        
        direito.esquerdo = nodo;
        nodo.pai = direito;
    }

    private void rotacaoDireita(Nodo nodo) {
        Nodo esquerdo = nodo.esquerdo;
        nodo.esquerdo = esquerdo.direito;
        
        if (esquerdo.direito != null) {
            esquerdo.direito.pai = nodo;
        }
        
        esquerdo.pai = nodo.pai;
        
        if (nodo.pai == null) {
            raiz = esquerdo;
        } else if (nodo == nodo.pai.direito) {
            nodo.pai.direito = esquerdo;
        } else {
            nodo.pai.esquerdo = esquerdo;
        }
        
        esquerdo.direito = nodo;
        nodo.pai = esquerdo;
    }

    public boolean estaVazia() {
        return raiz == null;
    }

    public void mostrarArvore() {
        if (estaVazia()) {
            System.out.println("Árvore vazia");
            return;
        }
        mostrarArvoreRecursivo(raiz, 0);
    }

    private void mostrarArvoreRecursivo(Nodo nodo, int nivel) {
        if (nodo != null) {
            mostrarArvoreRecursivo(nodo.direito, nivel + 1);
            for (int i = 0; i < nivel; i++) {
                System.out.print("    ");
            }
            System.out.println(nodo.reserva.getIdReserva() + " (" + nodo.cor + ")");
            mostrarArvoreRecursivo(nodo.esquerdo, nivel + 1);
        }
    }

    public static String gerarNovoId() {
        return "RES" + String.format("%03d", nextId++);
    }
    
    public List<List<String>> getVisualizacaoGrid() {
        List<List<String>> grid = new ArrayList<>();
        int numLinhas = 5;
        int numColunas = 10;
        
        // Inicializar grid vazio
        for (int i = 0; i < numLinhas; i++) {
            List<String> linha = new ArrayList<>();
            for (int j = 0; j < numColunas; j++) {
                linha.add("Disponível - "); // Quarto disponível
            }
            grid.add(linha);
        }
        
        // Marcar quartos ocupados
        List<Reserva> reservasAtivas = listarReservasAtivas();
        for (Reserva reserva : reservasAtivas) {
            int numeroQuarto = reserva.getQuarto().getNumero();
            int linha = (numeroQuarto - 1) / numColunas;
            int coluna = (numeroQuarto - 1) % numColunas;
            if (linha < numLinhas && coluna < numColunas) {
                grid.get(linha).set(coluna, "Ocupado - "); // Quarto ocupado
            }
        }
        
        return grid;
    }
    public String getCorReserva(String idReserva) {
        Nodo nodo = buscarNodo(raiz, idReserva);
        if (nodo != null) {
            return nodo.cor == Cor.VERMELHO ? "VERMELHO" : "PRETO";
        }
        return "COR NÃO ENCONTRADA";
    }
}