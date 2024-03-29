package KN_14_5_Serbina.usermanagement.gui;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mockobjects.dynamic.Mock;

import junit.extensions.jfcunit.JFCTestCase;
import junit.extensions.jfcunit.JFCTestHelper;
import junit.extensions.jfcunit.eventdata.JTableMouseEventData;
import junit.extensions.jfcunit.eventdata.MouseEventData;
import junit.extensions.jfcunit.eventdata.StringEventData;
import junit.extensions.jfcunit.finder.DialogFinder;
import junit.extensions.jfcunit.finder.NamedComponentFinder;
import KN_14_5_Serbina.usermanagement.User;
import KN_14_5_Serbina.usermanagement.db.DaoFactory;
import KN_14_5_Serbina.usermanagement.db.DaoFactoryImpl;
import KN_14_5_Serbina.usermanagement.db.MockDaoFactory;
import KN_14_5_Serbina.usermanagement.util.Messages;

public class MainFrameTest extends JFCTestCase {

	private MainFrame mainFrame;
	private Mock mockUserDao;
	private ArrayList users;
	private User expectedUser;
	private User user;

	@Override
	@Before
	protected void setUp() throws Exception {
		super.setUp();
		try {

			Properties properties = new Properties();
			properties.setProperty("dao.factory", MockDaoFactory.class.getName());
			DaoFactory.init(properties);
			mockUserDao = ((MockDaoFactory) DaoFactory.getInstance()).getMockUserDao();
			setHelper(new JFCTestHelper());
			String firstName = "Patrick";
			String lastName = "Rotfuss";
			Date now = new Date( new Timestamp(2016, 12, 03, 0, 0, 0, 0).getTime());
			expectedUser = new User(20L, firstName, lastName, now);
			user = new User(20L, firstName, lastName, now);
			users = new ArrayList();
			users.add(expectedUser);

			mockUserDao.expectAndReturn("findAll", users);
			mainFrame = new MainFrame();
			mainFrame.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	@After
	protected void tearDown() throws Exception {
		try {
			mockUserDao.verify();
			mainFrame.setVisible(false);
			getHelper().cleanUp(this);
			super.tearDown();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Component find(Class componentClass, String name) {
		NamedComponentFinder finder;
		finder = new NamedComponentFinder(componentClass, name);
		finder.setWait(0);
		Component component = finder.find(mainFrame, 0);
		assertNotNull("Could not find component '" + name + "'", component);
		return component;
	}

	@Test
	public void testBrowseControls() {
		find(JPanel.class, "browsePanel");
		JTable table = (JTable) find(JTable.class, "userTable");
		assertEquals(3, table.getColumnCount());
		assertEquals(Messages.getString("UserTableModel.id"), table.getColumnName(0));
		assertEquals(Messages.getString("UserTableModel.first_name"), table.getColumnName(1));
		assertEquals(Messages.getString("UserTableModel.last_name"), table.getColumnName(2));
		find(JButton.class, "addButton");
		find(JButton.class, "editButton");
		find(JButton.class, "deleteButton");
		find(JButton.class, "detailsButton");

	}

	@Test
	public void testAddUser() {
		try {
			String firstName = "Max";
			String lastName = "Brooks";
			Date now = new Date();

			User user = new User(firstName, lastName, now);
			User expectedUser = new User(1L, firstName, lastName, now);
			mockUserDao.expectAndReturn("create", user, expectedUser);

			users.add(expectedUser);
			mockUserDao.expectAndReturn("findAll", users);

			JTable table = (JTable) find(JTable.class, "userTable");
			assertEquals(1, table.getRowCount());
			JButton addButton = (JButton) find(JButton.class, "addButton");
			getHelper().enterClickAndLeave(new MouseEventData(this, addButton));
			find(JPanel.class, "addPanel");
			JTextField firstNameField = (JTextField) find(JTextField.class, "firstNameField");
			JTextField lastNameField = (JTextField) find(JTextField.class, "lastNameField");
			JTextField dateOfBirthField = (JTextField) find(JTextField.class, "dateOfBirthField");
			JButton okButton = (JButton) find(JButton.class, "okButton");
			find(JButton.class, "cancelButton");

			getHelper().sendString(new StringEventData(this, firstNameField, firstName));
			getHelper().sendString(new StringEventData(this, lastNameField, lastName));
			DateFormat formatter = DateFormat.getInstance();
			String date = formatter.format(now);
			getHelper().sendString(new StringEventData(this, dateOfBirthField, date));

			getHelper().enterClickAndLeave(new MouseEventData(this, okButton));

			find(JPanel.class, "browsePanel");
			table = (JTable) find(JTable.class, "userTable");
			assertEquals(2, table.getRowCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testDeleteUser() {
		try {
			
			mockUserDao.expectAndReturn("delete", expectedUser, expectedUser);
			mockUserDao.expectAndReturn("find", 20L, expectedUser);
			mockUserDao.expectAndReturn("find", 20L, expectedUser);
			mockUserDao.expectAndReturn("find", 20L, expectedUser);
			users.remove(expectedUser);
			mockUserDao.expectAndReturn("findAll", users);

			JTable table = (JTable) find(JTable.class, "userTable");
			assertEquals(1, table.getRowCount());
			JButton deleteButton = (JButton) find(JButton.class, "deleteButton");
			getHelper().enterClickAndLeave(new JTableMouseEventData(this, table, 0, 0, 1));
			getHelper().enterClickAndLeave(new MouseEventData(this, deleteButton));
			find(JPanel.class, "deletePanel");
			find(JLabel.class, "firstNameLabel");
			find(JLabel.class, "lastNameLabel");
			find(JLabel.class, "dateOfBirthLabel");

			JButton okButton = (JButton) find(JButton.class, "okButton");
			find(JButton.class, "cancelButton");
			getHelper().enterClickAndLeave(new MouseEventData(this, okButton));

			find(JPanel.class, "browsePanel");
			table = (JTable) find(JTable.class, "userTable");
			assertEquals(0, table.getRowCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDetailsUser() {
		try {
			mockUserDao.expectAndReturn("find", 20L, expectedUser);
			mockUserDao.expectAndReturn("find", 20L, expectedUser);
			mockUserDao.expectAndReturn("find", 20L, expectedUser);
			JTable table = (JTable) find(JTable.class, "userTable");
			assertEquals(1, table.getRowCount());
			JButton detailsButton = (JButton) find(JButton.class, "detailsButton");
			getHelper().enterClickAndLeave(new JTableMouseEventData(this, table, 0, 0, 1));
			getHelper().enterClickAndLeave(new MouseEventData(this, detailsButton));
			find(JPanel.class, "detailsPanel");
			JLabel firstNameLabel=(JLabel) find(JLabel.class, "firstNameLabel");
			JLabel lastNameLabel=(JLabel) find(JLabel.class, "lastNameLabel");
			JLabel dateOfBirthLabel =(JLabel) find(JLabel.class, "dateOfBirthLabel");

			DateFormat formatter = DateFormat.getDateInstance();
			assertEquals(expectedUser.getFirstName(),firstNameLabel.getText());
			assertEquals(expectedUser.getLastName(),lastNameLabel.getText());
			assertEquals(formatter.format(expectedUser.getDateOfBirth()),dateOfBirthLabel.getText());
			JButton okButton = (JButton) find(JButton.class, "okButton");
			mockUserDao.expectAndReturn("findAll", users);
			getHelper().enterClickAndLeave(new MouseEventData(this, okButton));

			find(JPanel.class, "browsePanel");
			table = (JTable) find(JTable.class, "userTable");
			assertEquals(1, table.getRowCount());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testEditUser() {
		try {
			String firstName = "Stephen";
			String lastName = "King";
			Date dateOfBirth = new Date(1963,11,23);
			User expectedUser = new User (20L, firstName, lastName, dateOfBirth);
			mockUserDao.expectAndReturn("update", user, expectedUser);
			users.remove(user);
			users.add(expectedUser);
			mockUserDao.expectAndReturn("findAll", users);
			mockUserDao.expectAndReturn("find", 20L, this.expectedUser);
			mockUserDao.expectAndReturn("find", 20L, expectedUser);
			mockUserDao.expectAndReturn("find", 20L, expectedUser);

			JTable table = (JTable) find(JTable.class, "userTable");
			assertEquals(1, table.getRowCount());
			JButton editButton = (JButton) find(JButton.class, "editButton");
			getHelper().enterClickAndLeave(new JTableMouseEventData(this, table, 0, 0, 1));
			getHelper().enterClickAndLeave(new MouseEventData(this, editButton));
			find(JPanel.class, "editPanel");
			JTextField firstNameField = (JTextField) find(JTextField.class, "firstNameField");
			JTextField lastNameField = (JTextField) find(JTextField.class, "lastNameField");
			JTextField dateOfBirthField = (JTextField) find(JTextField.class, "dateOfBirthField");
			JButton okButton = (JButton) find(JButton.class, "okButton");
			find(JButton.class, "cancelButton");
			
			firstNameField.setText("");
			lastNameField.setText("");
			dateOfBirthField.setText("");
			getHelper().sendString(new StringEventData(this, firstNameField, firstName));
			getHelper().sendString(new StringEventData(this, lastNameField, lastName));
			DateFormat formatter = DateFormat.getDateInstance();
			String date = formatter.format(dateOfBirth);
			getHelper().sendString(new StringEventData(this, dateOfBirthField, date));
			getHelper().enterClickAndLeave(new MouseEventData(this, okButton));

			find(JPanel.class, "browsePanel");
			table = (JTable) find(JTable.class, "userTable");
			assertEquals(1, table.getRowCount());
			assertEquals(expectedUser.getId(),table.getValueAt(0, 0));
			assertEquals(expectedUser.getFirstName(),table.getValueAt(0, 1));
			assertEquals(expectedUser.getLastName(),table.getValueAt(0, 2));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

