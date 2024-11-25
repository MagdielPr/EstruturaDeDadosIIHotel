package SistemaHotelEstrutura2;

public class Usuario {
	 private String CPF;
	    private String nome;
	    private String telefone;  
	    private String email;     
	    
	    public Usuario(String CPF, String nome, String telefone, String email) {
	        this.CPF = CPF;
	        this.nome = nome;
	        this.telefone = telefone;
	        this.email = email;
	    }

		public String getCPF() {
			return CPF;
		}

		public void setCPF(String CPF) {
			this.CPF = CPF;
		}

		public String getNome() {
			return nome;
		}

		public void setNome(String nome) {
			this.nome = nome;
		}

		public String getTelefone() {
			return telefone;
		}

		public void setTelefone(String telefone) {
			this.telefone = telefone;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}
}
