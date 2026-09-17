package com.paciente.bean;

import com.paciente.dao.PacienteDAO;
import com.paciente.model.Paciente;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteBean {

    private Paciente paciente;
    private final PacienteDAO pacienteDAO = new PacienteDAO();
    private List<Paciente> pacientes;
    private String mensagem;

    private FacesContext getFacesContext() {
        return FacesContext.getCurrentInstance();
    }

    private void redirecionar(String pagina) {
        try {
            getFacesContext().getExternalContext().redirect(pagina);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void novoPaciente() {
        this.paciente = new Paciente();
        redirecionar("cadastro.jsf");
    }

    public void listarPacientes() {
        redirecionar("relatorio.jsf");
    }

    public void voltarIndex() {
        redirecionar("index.jsf");
    }

    public void cancelar() {
        redirecionar("relatorio.jsf");
    }

    public void editar(Paciente p) {
        redirecionar("cadastro.jsf?id=" + p.getId());
    }

    public void salvar() {
        if (paciente == null ||
                isVazio(paciente.getNome()) ||
                isVazio(paciente.getCpf()) ||
                isVazio(paciente.getTelefone()) ||
                paciente.getIdade() == null) {

            this.mensagem = "Não é possível cadastrar com valores em branco. Preencha todos os campos!";
            return;
        }

        // Não cadastrar duas pessoas com o mesmo nome: testado e válidado.
        try {
            Paciente existente = pacienteDAO.buscarPorNome(paciente.getNome().trim());
            if (existente != null && (paciente.getId() == null || !existente.getId().equals(paciente.getId()))) {
                this.mensagem = "Já existe um paciente cadastrado com o nome '" + paciente.getNome().trim() + "'!";
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            this.mensagem = "Erro ao verificar duplicidade: " + e.getMessage();
            return;
        }

        // Formatação do telefone em Desenvolvimento....
        String cpfNumeros = paciente.getCpf().replaceAll("\\D", "");
        if (cpfNumeros.length() != 11) {
            this.mensagem = "CPF inválido! O CPF deve conter exatamente 11 números.";
            return;
        }
        paciente.setCpf(cpfNumeros.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4"));

        // Formatação do telefone em Desenvolvimento....
        String telNumeros = paciente.getTelefone().replaceAll("\\D", "");
        if (telNumeros.length() < 10 || telNumeros.length() > 11) {
            this.mensagem = "Telefone inválido! O telefone deve conter o DDD e ter 10 ou 11 números.";
            return;
        }
        String pattern = telNumeros.length() == 11 ? "(\\d{2})(\\d{5})(\\d{4})" : "(\\d{2})(\\d{4})(\\d{4})";
        paciente.setTelefone(telNumeros.replaceAll(pattern, "($1) $2-$3"));

        try {
            String nomeCadastrado = paciente.getNome().trim();
            pacienteDAO.salvar(this.paciente);
            this.paciente = new Paciente();

            String msgSucesso = URLEncoder.encode("Paciente '" + nomeCadastrado + "' salvo com sucesso!", "UTF-8");
            redirecionar("relatorio.jsf?msg=" + msgSucesso);
        } catch (Exception e) {
            e.printStackTrace();
            this.mensagem = "Erro ao salvar paciente: " + e.getMessage();
        }
    }

    public void excluir(Paciente p) {
        try {
            pacienteDAO.excluir(p.getId());
            String msgExclusao = URLEncoder.encode("Paciente '" + p.getNome() + "' excluído com sucesso!", "UTF-8");
            redirecionar("relatorio.jsf?msg=" + msgExclusao);
        } catch (Exception e) {
            e.printStackTrace();
            this.mensagem = "Erro ao excluir paciente: " + e.getMessage();
        }
    }

    private boolean isVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    public List<Paciente> getPacientes() {
        try {
            this.pacientes = pacienteDAO.listarTodos();
        } catch (SQLException e) {
            e.printStackTrace();
            this.pacientes = new ArrayList<>();
        }
        return this.pacientes;
    }

    public Paciente getPaciente() {
        if (paciente == null) {
            FacesContext context = getFacesContext();
            if (context != null && context.getExternalContext() != null) {
                String idParam = context.getExternalContext().getRequestParameterMap().get("id");
                if (!isVazio(idParam)) {
                    try {
                        paciente = pacienteDAO.buscarPorId(Integer.parseInt(idParam));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            if (paciente == null) {
                paciente = new Paciente();
            }
        }
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    public String getMensagem() {
        FacesContext context = getFacesContext();
        if (context != null && context.getExternalContext() != null) {
            String msgParam = context.getExternalContext().getRequestParameterMap().get("msg");
            if (!isVazio(msgParam)) {
                return msgParam;
            }
        }
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}