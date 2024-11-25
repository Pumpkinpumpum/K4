import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

// Класс OfficeEquipment для представления данных офисной техники
class OfficeEquipment {
    private String name;      // Название устройства
    private String condition; // Состояние устройства (например, "Работает", "Не работает")
    private String location;  // Местоположение устройства (например, "Кабинет 101")

    public OfficeEquipment(String name, String condition, String location) {
        this.name = name;
        this.condition = condition;
        this.location = location;
    }

    public String getName() {
        return name;
    }

    public String getCondition() {
        return condition;
    }

    public String getLocation() {
        return location;
    }

    public Object[] toTableRow() {
        return new Object[]{name, condition, location};
    }

    @Override
    public String toString() {
        return name + "," + condition + "," + location;
    }
}

// Класс OfficeEquipmentManager для управления техникой и выполнения операций
class OfficeEquipmentManager {
    private List<OfficeEquipment> equipmentList;

    public OfficeEquipmentManager() {
        equipmentList = new ArrayList<>();
    }

    public void readFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            equipmentList.clear();  // Очистка списка перед загрузкой новых данных
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length == 3) {
                    String name = data[0].trim();
                    String condition = data[1].trim();
                    String location = data[2].trim();
                    equipmentList.add(new OfficeEquipment(name, condition, location));
                }
            }
            JOptionPane.showMessageDialog(null, "Данные успешно загружены из файла.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при чтении файла: " + e.getMessage());
        }
    }

    public void saveToFile(String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (OfficeEquipment equipment : equipmentList) {
                bw.write(equipment.toString());
                bw.newLine();
            }
            JOptionPane.showMessageDialog(null, "Данные успешно сохранены в файл.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Ошибка при сохранении файла: " + e.getMessage());
        }
    }

    public void addEquipment(OfficeEquipment equipment) {
        equipmentList.add(equipment);
    }

    public void removeEquipment(int index) {
        if (index >= 0 && index < equipmentList.size()) {
            equipmentList.remove(index);
        }
    }

    public List<OfficeEquipment> getEquipmentList() {
        return equipmentList;
    }

    // Сортировка по состоянию
    public void sortByCondition() {
        equipmentList.sort(Comparator.comparing(OfficeEquipment::getCondition));
    }

    // Сортировка по местоположению
    public void sortByLocation() {
        equipmentList.sort(Comparator.comparing(OfficeEquipment::getLocation));
    }
}

// Класс Main с графическим интерфейсом
public class Main extends JFrame {
    private OfficeEquipmentManager manager;
    private JTable equipmentTable;
    private DefaultTableModel tableModel;

    public Main() {
        manager = new OfficeEquipmentManager();
        tableModel = new DefaultTableModel(new Object[]{"Название", "Состояние", "Местоположение"}, 0);
        equipmentTable = new JTable(tableModel);
        equipmentTable.setFillsViewportHeight(true);

        // Установка параметров окна
        setTitle("Управление офисной техникой");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Панель с кнопками
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(9, 1, 5, 5));
        panel.setBackground(new Color(0, 51, 102)); // Темно-синий фон панели

        // Создание кнопок с переводом на русский
        JButton readButton = new JButton("Загрузить из файла");
        JButton saveButton = new JButton("Сохранить в файл");
        JButton addButton = new JButton("Добавить технику");
        JButton deleteButton = new JButton("Удалить технику");
        JButton sortByConditionButton = new JButton("Сортировать по состоянию");
        JButton sortByLocationButton = new JButton("Сортировать по местоположению");

        // Настройка цвета кнопок
        Color buttonColor = new Color(0, 102, 204); // Синий фон кнопок
        Color buttonTextColor = Color.WHITE;        // Белый текст кнопок

        readButton.setBackground(buttonColor);
        saveButton.setBackground(buttonColor);
        addButton.setBackground(buttonColor);
        deleteButton.setBackground(buttonColor);
        sortByConditionButton.setBackground(buttonColor);
        sortByLocationButton.setBackground(buttonColor);

        readButton.setForeground(buttonTextColor);
        saveButton.setForeground(buttonTextColor);
        addButton.setForeground(buttonTextColor);
        deleteButton.setForeground(buttonTextColor);
        sortByConditionButton.setForeground(buttonTextColor);
        sortByLocationButton.setForeground(buttonTextColor);

        // Добавление кнопок на панель
        panel.add(readButton);
        panel.add(saveButton);
        panel.add(addButton);
        panel.add(deleteButton);
        panel.add(sortByConditionButton);
        panel.add(sortByLocationButton);

        // Добавление панели и таблицы в окно
        add(panel, BorderLayout.WEST);
        add(new JScrollPane(equipmentTable), BorderLayout.CENTER);

        // Установка фона таблицы
        equipmentTable.setBackground(Color.WHITE);
        equipmentTable.setForeground(new Color(0, 51, 102)); // Синий текст в таблице
        equipmentTable.getTableHeader().setBackground(new Color(0, 102, 204)); // Синий фон для заголовков
        equipmentTable.getTableHeader().setForeground(Color.WHITE); // Белый текст для заголовков

        // Обработчики событий для кнопок
        readButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            int result = fileChooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                manager.readFromFile(file.getAbsolutePath());
                updateTable(manager.getEquipmentList());
            }
        });

        saveButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
            int result = fileChooser.showSaveDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                String filePath = file.getAbsolutePath();

                // Проверка на расширение .txt, добавляем его, если отсутствует
                if (!filePath.toLowerCase().endsWith(".txt")) {
                    filePath += ".txt";
                }
                manager.saveToFile(filePath);
            }
        });

        addButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog("Введите название устройства:");
            if (name == null || name.isEmpty()) return;
            String condition = JOptionPane.showInputDialog("Введите состояние устройства (например, 'Работает', 'Не работает'):");
            if (condition == null || condition.isEmpty()) return;
            String location = JOptionPane.showInputDialog("Введите местоположение устройства:");
            if (location == null || location.isEmpty()) return;

            manager.addEquipment(new OfficeEquipment(name, condition, location));
            updateTable(manager.getEquipmentList());
        });

        deleteButton.addActionListener(e -> {
            int selectedRow = equipmentTable.getSelectedRow();
            if (selectedRow != -1) {
                int modelIndex = equipmentTable.convertRowIndexToModel(selectedRow);
                manager.removeEquipment(modelIndex);
                updateTable(manager.getEquipmentList());
                JOptionPane.showMessageDialog(null, "Оборудование удалено.");
            } else {
                JOptionPane.showMessageDialog(null, "Пожалуйста, выберите строку для удаления.");
            }
        });

        sortByConditionButton.addActionListener(e -> {
            manager.sortByCondition();
            updateTable(manager.getEquipmentList());
        });

        sortByLocationButton.addActionListener(e -> {
            manager.sortByLocation();
            updateTable(manager.getEquipmentList());
        });
    }

    // Метод для обновления данных в таблице
    private void updateTable(List<OfficeEquipment> equipmentList) {
        tableModel.setRowCount(0);  // Очищаем таблицу
        for (OfficeEquipment equipment : equipmentList) {
            tableModel.addRow(equipment.toTableRow());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Main frame = new Main();
            frame.setVisible(true);
        });
    }
}
