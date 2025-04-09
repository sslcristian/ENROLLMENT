package data;

import java.sql.Connection;
import java.util.ArrayList;
import model.Course;;

public class CourseDAO implements CRUD_Operation<Course,String> {
    private Connection connection;

    public CourseDAO(Connection connection) {
        this.connection = connection;
    }
	@Override
	public void save(Course course) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<Course> fetch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void update(Course course) {
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
