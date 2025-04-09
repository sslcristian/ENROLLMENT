package data;

import java.sql.Connection;
import java.util.ArrayList;
import model.Student;

public class StudentDAO implements CRUD_Operation<Student,String>{
	
    private Connection connection;

    public StudentDAO(Connection connection) {
        this.connection = connection;
    }
	@Override
	public void save(Student student) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Student> fetch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Student student) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void delete(String id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean authenticate(String id) {
		// TODO Auto-generated method stub
		return false;
	}

}
