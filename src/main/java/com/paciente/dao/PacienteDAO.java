package com.paciente.dao;

import com.paciente.model.Paciente;
import com.paciente.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PacienteDAO {

    public void salvar(Paciente paciente) throws SQLException {
        boolean novo = (paciente.getId() == null || paciente.getId() == 0);
        String sql = novo
                ? "INSERT INTO paciente (nome, idade, telefone, cpf) VALUES (?, ?, ?, ?)"
                : "UPDATE paciente SET nome=?, idade=?, telefone=?, cpf=? WHERE id=?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.getNome());
            stmt.setObject(2, paciente.getIdade());
            stmt.setString(3, paciente.getTelefone());
            stmt.setString(4, paciente.getCpf());

            if (!novo) {
                stmt.setInt(5, paciente.getId());
            }

            stmt.executeUpdate();
        }
    }

    public List<Paciente> listarTodos() throws SQLException {
        List<Paciente> lista = new ArrayList<>();
        String sql = "SELECT * FROM paciente ORDER BY id DESC";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                lista.add(mapearPaciente(rs));
            }
        }
        return lista;
    }

    public void excluir(Integer id) throws SQLException {
        String sql = "DELETE FROM paciente WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public Paciente buscarPorId(Integer id) throws SQLException {
        String sql = "SELECT * FROM paciente WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
        }
        return null;
    }

    public Paciente buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM paciente WHERE LOWER(nome) = LOWER(?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nome.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapearPaciente(rs);
                }
            }
        }
        return null;
    }

    private Paciente mapearPaciente(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getInt("id"));
        p.setNome(rs.getString("nome"));
        p.setIdade(rs.getObject("idade") != null ? rs.getInt("idade") : null);
        p.setTelefone(rs.getString("telefone"));
        p.setCpf(rs.getString("cpf"));
        return p;
    }
}