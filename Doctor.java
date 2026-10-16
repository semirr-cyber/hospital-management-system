import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class Doctor extends JFrame {

    JTextField idF, nameF, ageF, phoneF, addressF;
    JTextField departmentF, qualificationF, experienceF;

    JComboBox<String> genderBox;
    JComboBox<String> specializationBox;

    JButton addBtn, updateBtn, deleteBtn, clearBtn, backBtn;

    JTable table;
    DefaultTableModel model;


    public Doctor() {

        setTitle("Hospital Management System - Doctor Management");
        setSize(950,650);
        setLayout(null);
        setLocationRelativeTo(null);


        // page title
        JLabel title = new JLabel("Doctor Management");
        title.setFont(new Font("Arial", Font.BOLD, 26));
        title.setBounds(330, 20, 300, 40);
        add(title);

        // personal info labels and fields
        JLabel personal = new JLabel("Personal Information");
        personal.setFont(new Font("Arial", Font.BOLD, 16));
        personal.setBounds(40, 80, 250, 30);
        add(personal);



        JLabel name = new JLabel("Doctor Name:");
        name.setBounds(40,130,100,25);
        add(name);

        nameF = new JTextField();
        nameF.setBounds(150,130,220,25);
        add(nameF);



        JLabel age = new JLabel("Age:");
        age.setBounds(40,170,100,25);
        add(age);

        ageF = new JTextField();
        ageF.setBounds(150,170,220,25);
        add(ageF);



        JLabel gender = new JLabel("Gender:");
        gender.setBounds(40,210,100,25);
        add(gender);


        genderBox = new JComboBox<>(
                new String[]{
                        "Male",
                        "Female"
                }
        );

        genderBox.setBounds(150,210,220,25);
        add(genderBox);



        JLabel phone = new JLabel("Phone:");
        phone.setBounds(40,250,100,25);
        add(phone);


        phoneF = new JTextField();
        phoneF.setBounds(150,250,220,25);
        add(phoneF);



        JLabel address = new JLabel("Address:");
        address.setBounds(40,290,100,25);
        add(address);


        addressF = new JTextField();
        addressF.setBounds(150,290,220,25);
        add(address);





        // PROFESSIONAL INFORMATION


        JLabel professional =
                new JLabel("Professional Information");

        professional.setFont(
                new Font("Arial",Font.BOLD,16)
        );

        professional.setBounds(520,80,250,30);

        add(professional);




        JLabel id = new JLabel("Doctor ID:");

        id.setBounds(500,130,120,25);

        add(id);


        idF = new JTextField();

        idF.setBounds(650,130,220,25);

        add(idF);




        JLabel specialization =
                new JLabel("Specialization:");

        specialization.setBounds(500,170,120,25);

        add(specialization);



        specializationBox =
                new JComboBox<>(
                        new String[]{
                                "Cardiology",
                                "Neurology",
                                "Pediatrics",
                                "Dermatology",
                                "Orthopedics",
                                "General Medicine"
                        }
                );


        specializationBox.setBounds(650,170,220,25);

        add(specializationBox);




        JLabel department =
                new JLabel("Department:");

        department.setBounds(500,210,120,25);

        add(department);



        departmentF = new JTextField();

        departmentF.setBounds(650,210,220,25);

        add(departmentF);




        JLabel qualification =
                new JLabel("Qualification:");

        qualification.setBounds(500,250,120,25);

        add(qualification);



        qualificationF = new JTextField();

        qualificationF.setBounds(650,250,220,25);

        add(qualificationF);




        JLabel experience =
                new JLabel("Experience:");

        experience.setBounds(500,290,120,25);

        add(experience);



        experienceF = new JTextField();

        experienceF.setBounds(650,290,220,25);

        add(experienceF);





        // buttons
        addBtn = new JButton("Add Doctor");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        clearBtn = new JButton("Clear");
        backBtn = new JButton("Back");

        addBtn.setBounds(80, 360, 120, 35);
        updateBtn.setBounds(220, 360, 120, 35);
        deleteBtn.setBounds(360, 360, 120, 35);
        clearBtn.setBounds(500, 360, 120, 35);
        backBtn.setBounds(650, 360, 120, 35);

        add(addBtn);
        add(updateBtn);
        add(deleteBtn);
        add(clearBtn);
        add(backBtn);






        // TABLE


        String columns[] = {

                "ID",
                "Name",
                "Age",
                "Gender",
                "Phone",
                "Department",
                "Specialization"

        };


        model = new DefaultTableModel(columns,0);


        table = new JTable(model);


        JScrollPane scroll =
                new JScrollPane(table);


        scroll.setBounds(40,430,850,150);


        add(scroll);







        // add doctor
        addBtn.addActionListener(e -> {
            model.addRow(new Object[]{
                    idF.getText(),
                    nameF.getText(),
                    ageF.getText(),
                    genderBox.getSelectedItem(),
                    phoneF.getText(),
                    departmentF.getText(),
                    specializationBox.getSelectedItem()
            });
            JOptionPane.showMessageDialog(null, "Doctor added successfully.");
            clearForm();
        });








        // update doctor
        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                model.setValueAt(idF.getText(), row, 0);
                model.setValueAt(nameF.getText(), row, 1);
                model.setValueAt(ageF.getText(), row, 2);
                model.setValueAt(genderBox.getSelectedItem(), row, 3);
                model.setValueAt(phoneF.getText(), row, 4);
                model.setValueAt(departmentF.getText(), row, 5);
                model.setValueAt(specializationBox.getSelectedItem(), row, 6);
            } else {
                JOptionPane.showMessageDialog(null, "Select a doctor first.");
            }
        });







        // delete doctor
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) {
                model.removeRow(row);
            } else {
                JOptionPane.showMessageDialog(null, "Select a doctor first.");
            }
        });






        // clear form
        clearBtn.addActionListener(e -> clearForm());








        // back to dashboard
        backBtn.addActionListener(e -> {
            new Dashboard();
            dispose();
        });







        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        setVisible(true);

    }







    public void clearForm() {
        idF.setText("");
        nameF.setText("");
        ageF.setText("");
        phoneF.setText("");
        addressF.setText("");
        departmentF.setText("");
        qualificationF.setText("");
        experienceF.setText("");
    }






    public static void main(String[] args){


        new Doctor();


    }

}