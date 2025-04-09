package data;

import java.sql.Connection;
import java.util.ArrayList;

import model.Enrollment;

public class EnrollmentDAO {
    private Connection connection;

    public EnrollmentDAO(Connection connection) {
        this.connection = connection;
    }

	public void save(Enrollment enrollment) {
		// TODO Auto-generated method stub

	}


	public ArrayList<Enrollment> fetch() {
		// TODO Auto-generated method stub
		return null;
	}


	public void update(Enrollment enrollment) {
		// TODO Auto-generated method stub

	}


	public void delete(String id, String code) {
		// TODO Auto-generated method stub

	}

	public boolean authenticate(String id, String code) {
		// TODO Auto-generated method stub
		return false;
	}

}
