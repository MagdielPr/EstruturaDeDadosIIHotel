package SistemaHotelEstrutura2;

public class Quarto {
    private int numero;
    private CategoriaQuarto categoria;
    private double valorDiaria;
    private boolean disponivel;
    
    public enum CategoriaQuarto {
        ECONOMICO(100.0),
        LUXO(250.0),
        PRESIDENCIAL(500.0);
        
        private final double valorBase;
        
        CategoriaQuarto(double valorBase) {
            this.valorBase = valorBase;
        }
        
        public double getValorBase() {
            return valorBase;
        }
    }
    
    public Quarto(int numero, CategoriaQuarto categoria) {
        this.numero = numero;
        this.categoria = categoria;
        this.valorDiaria = categoria.getValorBase();
        this.disponivel = true;
    }

	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public CategoriaQuarto getCategoria() {
		return categoria;
	}

	public void setCategoria(CategoriaQuarto categoria) {
		this.categoria = categoria;
	}

	public double getValorDiaria() {
		return valorDiaria;
	}

	public void setValorDiaria(double valorDiaria) {
		this.valorDiaria = valorDiaria;
	}

	public boolean isDisponivel() {
		return disponivel;
	}

	public void setDisponivel(boolean disponivel) {
		this.disponivel = disponivel;
	}
    
    
}
