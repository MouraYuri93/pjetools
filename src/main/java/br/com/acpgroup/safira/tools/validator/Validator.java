package br.com.acpgroup.safira.tools.validator;

import br.com.acpgroup.safira.tools.MySQLDatabase;
import br.com.acpgroup.safira.tools.model.PjeModel;

import java.sql.SQLException;
import java.util.List;

public class Validator {

    private MySQLDatabase database;

    public Validator(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        this.database = new MySQLDatabase(dbUrl, dbUser, dbPassword);
    }

    public void validateModels(List<PjeModel> models) throws SQLException {
        for (PjeModel model : models) {
            Long id = database.doesRecordExist(model.getProcesso(), model.getNumeroDocumento());
            if(id != null ) {
                model.setIdSigad(id);
            }
        }
    }

    public void close() throws SQLException {
        database.close();
    }
}
