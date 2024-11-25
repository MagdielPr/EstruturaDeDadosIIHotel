package SistemaHotelEstrutura2;

import java.util.HashMap;

public class CadastroUsuarios {
    private HashMap<String, Usuario> usuarios;

    // Construtor
    public CadastroUsuarios() {
        this.usuarios = new HashMap<>();
    }

    // Adiciona um novo usuário ao sistema
    public void adicionarUsuario(Usuario usuario) {
        if (usuario == null || usuario.getCPF() == null || usuario.getCPF().isEmpty()) {
            throw new IllegalArgumentException("Usuário ou CPF inválido.");
        }

        if (usuarios.containsKey(usuario.getCPF())) {
            throw new IllegalArgumentException("Usuário com CPF " + usuario.getCPF() + " já existe.");
        }

        usuarios.put(usuario.getCPF(), usuario);
    }

    // Retorna um usuário com base no CPF
    public Usuario consultarUsuario(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            throw new IllegalArgumentException("CPF inválido.");
        }

        Usuario usuario = usuarios.get(cpf);
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário com CPF " + cpf + " não encontrado.");
        }

        return usuario;
    }
}
