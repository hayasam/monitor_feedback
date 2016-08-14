package ch.uzh.ifi.feedback.library.rest.Service;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ch.uzh.ifi.feedback.library.rest.Service.DbResultParser;
import ch.uzh.ifi.feedback.library.rest.Service.IDbService;
import ch.uzh.ifi.feedback.library.rest.annotations.DbIgnore;
import javassist.NotFoundException;

public abstract class ServiceBase<T> implements IDbService<T> {
	
	private Class<T> serviceClass;
	private String tableName;
	private String dbName;
	private List<IDbService<?>> childServices;
	private String selectedLanguage;
	
	protected DbResultParser<T> resultParser;
	
	public ServiceBase(
			DbResultParser<T> resultParser, 
			Class<T> serviceClass, 
			String tableName,
			String dbName,
			IDbService<?>... services)
	{
		this.serviceClass = serviceClass;
		this.tableName = tableName;
		this.resultParser = resultParser;
		this.childServices = new ArrayList<>();
		this.dbName = dbName;
		for(IDbService<?> service : services)
		{
			childServices.add(service);
		}
	}
	
	@Override
	public void SetLanguage(String lang)
	{
		this.selectedLanguage = lang;
		childServices.stream().forEach(s -> s.SetLanguage(lang));
	}
	
	@Override
	public String GetLanguage()
	{
		return selectedLanguage;
	}
	
	@Override
	public T GetById(Connection con, int id) throws SQLException, NotFoundException 
	{
		ResultSet result = CheckId(con, id);
		T instance = null;
		try {
			instance = serviceClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		resultParser.SetFields(instance, result);
		return instance;
	}
	
	@Override
	public List<T> GetAll(Connection con) throws SQLException, NotFoundException
	{
		String statement = String.format("SELECT * FROM %s.%s ;", dbName, tableName);
		PreparedStatement s = con.prepareStatement(statement);
		ResultSet result = s.executeQuery();

		return getList(result);
	}
	
	@Override
	public void Delete(Connection con, int id) throws SQLException, NotFoundException
	{
		CheckId(con, id);
		
		String statement = String.format("DELETE * FROM %s.%s as t WHERE t.id = ? ;", dbName, tableName);
		PreparedStatement s = con.prepareStatement(statement);
		s.setInt(1, id);
		s.execute();
	}
	
	@Override
	public int Insert(Connection con, T object) throws SQLException ,NotFoundException ,UnsupportedOperationException 
	{
		String statement = String.format("INSERT INTO %s.%s (", dbName, tableName);
		Map<String, Field> fields = resultParser.GetFields();
		List<Object> fieldValues = new ArrayList<>();
		Iterator<Entry<String, Field>> iterator = fields.entrySet().iterator();
		
		while(iterator.hasNext())
		{
			Entry<String, Field> entry = iterator.next();
			try {
				Field field = entry.getValue();
				Object fieldValue = field.get(object);
				if(fieldValue != null && !field.getName().toLowerCase().equals("id") && !field.isAnnotationPresent(DbIgnore.class))
				{
					statement += "`" +entry.getKey()+ "`";
					statement += ", ";
					
					fieldValues.add(fieldValue);
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		statement = statement.substring(0, statement.length()-2);
		statement += ") VALUES (";
		for(int i=0; i<fieldValues.size(); ++i)
		{
			statement += "?";
			if(i < fieldValues.size()-1)
				statement += ", ";
		}
		statement += ") ;";
		
		PreparedStatement s = con.prepareStatement(statement, PreparedStatement.RETURN_GENERATED_KEYS);
		for(int i=0; i<fieldValues.size(); i++)
		{
			s.setObject(i+1, fieldValues.get(i));
		}
		
		s.execute();
	    ResultSet keys = s.getGeneratedKeys();
	    keys.next();
	    return keys.getInt(1);
	}
	
	public List<T> GetWhereEquals(Connection con, List<String> attributeNames, List<Object> values) throws SQLException
	{
		if(attributeNames.size() != values.size())
			return null;
		
		String statement = 
				  "SELECT * "
				+ "FROM %s.%s as t ";
		statement = String.format(statement, dbName, tableName);
		statement += "WHERE t.%s = ? ";
		
		for(int i=1; i<attributeNames.size(); i++)
		{
			statement += "AND t.%s = ? ";
		}
		statement += ";";
		statement = String.format(statement, attributeNames.toArray());
		
		PreparedStatement s = con.prepareStatement(statement);
		for(int i=0; i<values.size(); i++)
		{
			s.setObject(i+1, values.get(i));
		}
		ResultSet result = s.executeQuery();
		return getList(result);
	}
	
	protected List<T> GetAllFor(Connection con, String foreignTableName, String foreignKeyName, int foreignKey)
			throws SQLException, NotFoundException
	{
	    String statement = String.format(
    		    "SELECT * "
    		  + "FROM %s.%s as f "
    		  + "JOIN %s.%s as t ON t.%s = f.id "
    		  + "WHERE f.id = ? ;", dbName, foreignTableName, dbName, tableName, foreignKeyName);
	    
	    PreparedStatement s = con.prepareStatement(statement);
	    s.setInt(1, foreignKey);
	    ResultSet result = s.executeQuery();
	    
	    return getList(result);
	}
	
	protected ResultSet CheckId(Connection con, int id) throws SQLException, NotFoundException
	{
		String statement = String.format("SELECT * FROM %s.%s as t WHERE t.id = ? ;", dbName, tableName);
		PreparedStatement s = con.prepareStatement(statement);
		s.setInt(1, id);
		
		ResultSet result = s.executeQuery();
		
		if(!result.next())
			throw new NotFoundException("Table '" + tableName + "' does not contain an object with id " + id);
		
		return result;
	}
	
	private List<T> getList(ResultSet result) throws SQLException
	{
		List<T> list = new ArrayList<>();
		while(result.next())
		{
			try {
				T instance = serviceClass.newInstance();
				resultParser.SetFields(instance, result);
				list.add(instance);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}
}
