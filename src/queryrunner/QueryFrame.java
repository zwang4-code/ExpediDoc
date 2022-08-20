/**
 * Group 6 - Milestone 3
 * Date: May 31, 2021
 * Students: Zi Wang, Dominic Burgi, Luoshan Zhang
 *
 * @author Professor M. Mckee, Zi Wang, Dominic Burgi, Luoshan Zhang
 */

package queryrunner;

import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class QueryFrame extends JFrame {

    /**
     * Constructor.
     *
     * @param queryrunnerObj A QueryRunner object.
     */
    public QueryFrame(QueryRunner queryrunnerObj) {
        queryrunner = queryrunnerObj;

        initComponents();
        initWelcomeLogo();
        setLayOut();

        int nAmt = queryrunner.GetTotalQueries();
        for (int i = 0; i < nAmt; i++) {
            this.comboBoxQueryNum.addItem("Query " + (i + 1));
        }
        comboBoxQueryNum.setEnabled(false);
        runQueryButton.setEnabled(false);
    }

    /**
     * The initComponents method initializes private fields and collects action from
     * the Connection and Run Query buttons.
     */
    private void initComponents() {
        passwordField = new JPasswordField();
        textFieldDatabase = new JTextField();
        textHostname = new JTextField();
        textFieldUser = new JTextField();

        queryNamesScrollBox = new JScrollPane();
        queryNamesTxt = new JTextArea();
        comboBoxQueryNum = new JComboBox<>();

        paramTextBox1 = new JTextField();
        paramTextBox2 = new JTextField();
        paramTextBox3 = new JTextField();
        paramTextBox4 = new JTextField();
        paramTextBoxes = new JTextField[] {paramTextBox1, paramTextBox2, paramTextBox3, paramTextBox4};
        paramInput = new JTextField();

        paramPick1 = new JComboBox<>();
        paramPick2 = new JComboBox<>();
        paramPick3 = new JComboBox<>();
        paramPick4 = new JComboBox<>();
        comboParamPicks = new JComboBox[]{paramPick1, paramPick2, paramPick3, paramPick4};

        paramNameLb1 = new JLabel();
        paramNameLb2 = new JLabel();
        paramNameLb3 = new JLabel();
        paramNameLb4 = new JLabel();
        paramNames = new JLabel[]{paramNameLb1, paramNameLb2, paramNameLb3, paramNameLb4};
        paramSectionPanel = new JPanel();

        tableBox = new JPanel();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new AbsoluteLayout());

        runQueryButton = new JButton();
        runQueryButton.setText("Run Query");
        runQueryButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                showQueryResults(evt);
            }
        });
        getContentPane().add(runQueryButton, new AbsoluteConstraints(580, 165, -1, -1));

        connectButton = new JButton();
        connectButton.setText("Connect");
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                logInAndConnect(evt);
            }
        });
        getContentPane().add(connectButton, new AbsoluteConstraints(270, 165, -1, -1));


        comboBoxQueryNum.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                getNewQuerySetParameters(evt);
            }
        });
        getContentPane().add(comboBoxQueryNum, new AbsoluteConstraints(410, 165, -1, -1));

    }

    /**
     * The initWelcomeLogo method displays the our product logo and picture to the GUI.
     */
    private void initWelcomeLogo() {
        JLabel lbLogo = new JLabel("");
        lbLogo.setIcon(new ImageIcon(new ImageIcon(QueryFrame.class.getResource(
                "/image/expediDoc_logo.png")).getImage().getScaledInstance(
                220, 100, Image.SCALE_SMOOTH)));
        getContentPane().add(lbLogo, new AbsoluteConstraints(10, 10, -1, -1));
        getContentPane().add(lbLogo, new AbsoluteConstraints(10, 10, -1, -1));

        JLabel lbPic = new JLabel("");
        lbPic.setIcon(new ImageIcon(new ImageIcon(QueryFrame.class.getResource(
                "/image/welcome_logo.png")).getImage().getScaledInstance(
                130, 150, Image.SCALE_SMOOTH)));
        getContentPane().add(lbPic, new AbsoluteConstraints(235, 10, -1, -1));

    }

    /**
     * The setLayOut method sets the layout for the tables and labels for the GUI.
     */
    private void setLayOut() {
        JLabel hostNameLabel = new JLabel();
        JLabel userLabel = new JLabel();
        JLabel pssdLabel = new JLabel();
        JLabel paramTitleLb = new JLabel();
        JLabel queryTitleLb = new JLabel();

        hostNameLabel.setText("Hostname");
        hostNameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        getContentPane().add(hostNameLabel, new AbsoluteConstraints(20, 120, 90, -1));
        textHostname.setNextFocusableComponent(textFieldUser);
        getContentPane().add(textHostname, new AbsoluteConstraints(100, 115, 120, -1));

        userLabel.setText("User");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        getContentPane().add(userLabel, new AbsoluteConstraints(20, 145, 90, -1));
        textFieldUser.setNextFocusableComponent(passwordField);
        getContentPane().add(textFieldUser, new AbsoluteConstraints(100, 140, 120, -1));

        // Box 5.3 <<password>> box
        pssdLabel.setText("Password");
        pssdLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        getContentPane().add(pssdLabel, new AbsoluteConstraints(20, 170, 90, -1));
        passwordField.setText("jPasswordField1");
        passwordField.setNextFocusableComponent(textFieldDatabase);
        getContentPane().add(passwordField, new AbsoluteConstraints(100, 165, 120, -1));

        paramTitleLb.setText("Parameter Choices");
        paramTitleLb.setFont(new Font("Arial", Font.BOLD, 15));
        getContentPane().add(paramTitleLb, new AbsoluteConstraints(20, 205, -1, -1));

        queryTitleLb.setText("Medical Appointment Queries");
        queryTitleLb.setFont(new Font("Arial", Font.BOLD, 17));
        getContentPane().add(queryTitleLb, new AbsoluteConstraints(400, 10, -1, -1));

        queryNamesTxt.setColumns(1);
        queryNamesTxt.setLineWrap(false); // changed from true to false, allow scroll horizontally
        queryNamesTxt.setRows(16);
        setAllQueryNames();
        getContentPane().add(queryNamesScrollBox, new AbsoluteConstraints(370, 35, 310, 130));


        paramSectionPanel.setLayout(new AbsoluteLayout());
        paramSectionPanel.setBackground(new Color(42, 145, 191));
        getContentPane().add(paramSectionPanel, new AbsoluteConstraints(10, 200, 215, 230));

        tableBox.setLayout(new BorderLayout());
        tableBox.setBackground(new Color(42, 145, 191));
        getContentPane().add(tableBox, new AbsoluteConstraints(235, 200, 450, 230));

        pack();
    }

    /**
     * The setAllQueryNames method displays all the query names in the selected text field.
     */
    private void setAllQueryNames() {
        StringBuilder allQueryNames = new StringBuilder();
        for (int i = 0; i < queryrunner.GetTotalQueries(); i++) {
            allQueryNames.append("Query " + (i + 1) + " - ");
            allQueryNames.append(queryrunner.getQueryTitle(i) + "\n");
        }
        queryNamesTxt.setText(allQueryNames.toString());
        queryNamesScrollBox.setViewportView(queryNamesTxt);
    }
    /**
     * The logInandConnet method collects log in information from user.
     * @param evt An action event.
     */
    private void logInAndConnect(ActionEvent evt) {

        final String DATABASE = "group6_milestone3";
        boolean bOK = true;

        if (connectButton.getText() == "Connect") {
            bOK = queryrunner.Connect(this.textHostname.getText(), this.textFieldUser.getText(),
                  String.valueOf(this.passwordField.getPassword()), DATABASE);

            if (bOK == true) {
                connectButton.setText("Disconnect");
                comboBoxQueryNum.setEnabled(true);
                runQueryButton.setEnabled(true);
            }
        } else {
            bOK = queryrunner.Disconnect();
            if (bOK == true) {
                connectButton.setText("Connect");
                comboBoxQueryNum.setEnabled(true);
                runQueryButton.setEnabled(true);
            }
        }
        if (bOK == false) {
            this.queryNamesTxt.setText(queryrunner.GetError());
            toResetQueryNames = true;
        }
    }

    /**
     * The getNewQuery gets the user's query choice and sets up the parameter section of
     * the GUI.
     * @param evt
     */
    private void getNewQuerySetParameters(ActionEvent evt) {
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i].setVisible(false);
            paramPick1.setVisible(false);
            paramPick2.setVisible(false);
            paramPick3.setVisible(false);
            paramPick4.setVisible(false);
            paramTextBoxes[i].setVisible(false);
        }

        paramTextBox1 = new JTextField();
        paramTextBox2 = new JTextField();
        paramTextBox3 = new JTextField();
        paramTextBox4 = new JTextField();
        paramTextBoxes = new JTextField[] {paramTextBox1, paramTextBox2, paramTextBox3, paramTextBox4};

        paramNameLb1 = new JLabel();
        paramNameLb2 = new JLabel();
        paramNameLb3 = new JLabel();
        paramNameLb4 = new JLabel();
        paramNames = new JLabel[]{paramNameLb1, paramNameLb2, paramNameLb3, paramNameLb4};

        paramPick1 = new JComboBox<>();
        paramPick2 = new JComboBox<>();
        paramPick3 = new JComboBox<>();
        paramPick4 = new JComboBox<>();
        comboParamPicks = new JComboBox[]{paramPick1, paramPick2, paramPick3, paramPick4};

        tableBox.setVisible(true);

        if (toResetQueryNames = true) {
            setAllQueryNames();
            toResetQueryNames = false;
        }

        String szChoice = (String) comboBoxQueryNum.getSelectedItem();
        String szStripChoice = szChoice.substring(6);
        queryChoice = Integer.parseInt(szStripChoice) - 1;

        if (queryrunner.isParameterQuery(queryChoice)) {
            int nAmt = queryrunner.GetParameterAmtForQuery(queryChoice);
            paramStringArray = new String[nAmt];

            for (int i = 0; i < nAmt; i++) {
                paramNames[i].setHorizontalAlignment(SwingConstants.RIGHT);
                paramNames[i].setVisible(true);
                paramNames[i].setText(queryrunner.GetParamText(queryChoice, i));
                comboParamPicks[i].setVisible(true);
            }
            if (nAmt >= 1) {
                paramSectionPanel.add(paramNames[0], new AbsoluteConstraints(13, 30, -1, -1));
                paramPick1 = setParamOptions(paramNames[0]);
                if (paramPick1.getSelectedIndex() == -1) {
                    paramTextBox1.setVisible(true);
                    paramSectionPanel.add(paramTextBox1, new AbsoluteConstraints(5, 48, 160, -1));
                }
                else {
                    paramSectionPanel.add(paramPick1, new AbsoluteConstraints(5, 48, 160, -1));
                    paramPick1.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            getInputParameter1(evt);
                        }
                    });
                }
            }
            if (nAmt >= 2) {
                paramSectionPanel.add(paramNames[1], new AbsoluteConstraints(13, 75, -1, -1));
                paramPick2 = setParamOptions(paramNames[1]);
                if (paramPick2.getSelectedIndex() == -1) {
                    paramTextBox2.setVisible(true);
                    paramSectionPanel.add(paramTextBox2, new AbsoluteConstraints(5, 93, 160, -1));
                }
                else {
                    paramSectionPanel.add(paramPick2, new AbsoluteConstraints(5, 93, 160, -1));
                    paramPick2.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            getInputParameter2(evt);
                        }
                    });
                }
            }
            if (nAmt >= 3) {
                paramSectionPanel.add(paramNames[2], new AbsoluteConstraints(13, 120, -1, -1));
                paramPick3 = setParamOptions(paramNames[2]);
                if (paramPick3.getSelectedIndex() == -1) {
                    paramTextBox3.setVisible(true);
                    paramSectionPanel.add(paramTextBox3, new AbsoluteConstraints(5, 138, 160, -1));
                }
                else {
                    paramSectionPanel.add(paramPick3, new AbsoluteConstraints(5, 138, 160, -1));
                    paramPick3.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            getInputParameter3(evt);
                        }
                    });
                }
            }
            if (nAmt >= 4) {
                paramSectionPanel.add(paramNames[3], new AbsoluteConstraints(13, 165, -1, -1));
                paramPick4 = setParamOptions(paramNames[3]);
                if (paramPick4.getSelectedIndex() == -1) {
                    paramTextBox4.setVisible(true);
                    paramSectionPanel.add(paramTextBox4, new AbsoluteConstraints(7, 183, 160, -1));
                }
                else {
                    paramSectionPanel.add(paramPick4, new AbsoluteConstraints(7, 183, 160, -1));
                    paramPick4.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            getInputParameter4(evt);
                        }
                    });
                }
            }
            for (int i = nAmt; i < paramNames.length; i++) {
                paramNames[i].setVisible(false);
                comboParamPicks[i].setVisible(false);
            }
        }
    }

    /**
     * The getParamOptions stores each parameter's choices in a JComboBox object.
     * @param paramName A JLabel object that holds the parameter's name.
     * @return A JComboBox object that holds the parameter's choices.
     */
    private JComboBox<String> setParamOptions(JLabel paramName) {
        JComboBox<String> allOptions = new JComboBox<>();
        String paramOption = queryrunner.getParameterOptions(paramName.getText());
        String arr[] = paramOption.split(" ", 2);
        if (arr[0].equals("Enter")) {
            return allOptions;
        }
        String[] paramOptions = new String[]{};
        paramOptions = paramOption.split("\\r?\\n");
        allOptions.addItem("Select\n");
        for (int i = 0; i < paramOptions.length; i++) {
            allOptions.addItem(paramOptions[i]);
        }
        return allOptions;
    }

    /**
     * The getInputParameter1 gets user's first parameter choice and stores it in an integer array.
     * @param evt An action event.
     */
    private void getInputParameter1(ActionEvent evt) {
        String paramChoice = (String) paramPick1.getSelectedItem();
        paramStringArray[0] = getDigitOrWord(paramChoice);
    }

    /**
     * The getInputParameter2 gets user's second parameter choice and stores it in an integer array.
     * @param evt An action event.
     */
    private void getInputParameter2(ActionEvent evt) {
        String paramChoice = (String) paramPick2.getSelectedItem();
        paramStringArray[1] = getDigitOrWord(paramChoice);
    }

    /**
     * The getInputParameter3 gets user's third parameter choice and stores it in an integer array.
     * @param evt An action event.
     */
    private void getInputParameter3(ActionEvent evt) {
        String paramChoice = (String) paramPick3.getSelectedItem();
        paramStringArray[2] = getDigitOrWord(paramChoice);
    }

    /**
     * The getInputParameter4 gets user's fourth parameter choice and stores it in an integer array.
     * @param evt An action event.
     */
    private void getInputParameter4(ActionEvent evt) {
        String paramChoice = (String) paramPick4.getSelectedItem();
        paramStringArray[3] = getDigitOrWord(paramChoice);
    }

    /**
     * The getDigit method converts the user's parameter choice into a string with only numbers.
     * @param paramChoice The parameter choice.
     * @return A string that carries the parameter choice number.
     */
    private String getDigitOrWord(String paramChoice) {
        StringBuilder userInput = new StringBuilder();

        if (!paramChoice.equals("Select") && !Character.isDigit(paramChoice.charAt(0)))
            userInput.append(paramChoice);
        else {
            for (int i = 0; i < paramChoice.length(); i++) {
                if (Character.isDigit(paramChoice.charAt(i)) || paramChoice.charAt(i) == '.') {
                    userInput.append(paramChoice.charAt(i));
                }
            }
        }
        return userInput.toString();
    }

    /**
     * The showQueryResults shows the query result or message to the GUI output panel.
     * @param evt An action event.
     */
    private void showQueryResults(ActionEvent evt) {
        String[] headers;
        String[][] allData;
        boolean bOK = true;

        for (int i = 0; i < paramTextBoxes.length; i++)
            if (!paramTextBoxes[i].getText().equals(""))
                paramStringArray[i] = paramTextBoxes[i].getText();

        if (!queryrunner.isParameterQuery(queryChoice))
            paramStringArray = new String[]{};

        if (queryrunner.isActionQuery(queryChoice)) {
            bOK = queryrunner.ExecuteUpdate(queryChoice, paramStringArray);
            if (bOK == true) {
                queryNamesTxt.setText("Rows affected = " + queryrunner.GetUpdateAmount());
                toResetQueryNames = true;
            } else {
                queryNamesTxt.setText(queryrunner.GetError());
                toResetQueryNames = true;
            }
        }
        else {
            bOK = queryrunner.ExecuteQuery(queryChoice, paramStringArray);
            if (bOK == true) {
                headers = queryrunner.GetQueryHeaders();
                allData = queryrunner.GetQueryData();

                if (scrollPane != null) {
                    scrollPane.remove(dataTable);
                    scrollPane.setVisible(false);
                }
                scrollPane = new JScrollPane();
                tableBox.setVisible(false);
                dataTable = new JTable(allData, headers);
                dataTable.setBounds(100, 100, 300, 100);
                Color blue = new Color(141, 196, 222);
                dataTable.setOpaque(false);
                dataTable.setBackground(blue);
                scrollPane.setVisible(true);
                scrollPane.setViewportView(dataTable);
                getContentPane().add(scrollPane, new AbsoluteConstraints(235, 200, 450, 230));

                this.setVisible(true);
            } else {
                this.queryNamesTxt.setText(queryrunner.GetError());
                toResetQueryNames = true;
            }
        }

    }

    private JButton connectButton;
    private JButton runQueryButton;

    private JPasswordField passwordField;
    private JTextField textFieldDatabase;
    private JTextField textHostname;
    private JTextField textFieldUser;
    private JTextField paramTextBox1;
    private JTextField paramTextBox2;
    private JTextField paramTextBox3;
    private JTextField paramTextBox4;
    private JTextField[] paramTextBoxes;
    private JTextField paramInput;

    private QueryRunner queryrunner;
    private JScrollPane scrollPane;
    private JScrollPane queryNamesScrollBox;
    private JPanel tableBox;
    private JTable dataTable;

    private JComboBox<String> comboBoxQueryNum;
    private JTextArea queryNamesTxt;
    private int queryChoice = 0;

    private JComboBox<String>[] comboParamPicks;
    private JComboBox<String> paramPick1;
    private JComboBox<String> paramPick2;
    private JComboBox<String> paramPick3;
    private JComboBox<String> paramPick4;

    private JLabel[] paramNames;
    private JLabel paramNameLb1 = new JLabel();
    private JLabel paramNameLb2 = new JLabel();
    private JLabel paramNameLb3 = new JLabel();
    private JLabel paramNameLb4 = new JLabel();
    private String[] paramStringArray;
    private JPanel paramSectionPanel;

    private boolean toResetQueryNames = false;
}
