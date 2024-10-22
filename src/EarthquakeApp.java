import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import org.json.JSONArray;
import org.json.JSONObject;

public class EarthquakeApp extends JFrame {

    private JPanel contentPane;
    private JTable table;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                EarthquakeApp frame = new EarthquakeApp();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public EarthquakeApp() {
        setTitle("Earthquake Data");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 400);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPane.setLayout(new BorderLayout(0, 0));
        setContentPane(contentPane);

        String[] columnNames = {"Magnitud", "Lugar", "Hora"};
        Object[][] data = fetchEarthquakeData();

        table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        contentPane.add(scrollPane, BorderLayout.CENTER);
    }

    private Object[][] fetchEarthquakeData() {
        Object[][] data = new Object[0][];
        try {
            String url = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2014-01-01&endtime=2014-01-02";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            connection.disconnect();

            // No necesitas JSONParser, puedes usar directamente JSONObject y JSONArray
            JSONObject jsonResponse = new JSONObject(content.toString());
            JSONArray features = jsonResponse.getJSONArray("features");

            data = new Object[features.length()][3];
            for (int i = 0; i < features.length(); i++) {
                JSONObject feature = features.getJSONObject(i);
                JSONObject properties = feature.getJSONObject("properties");

                double magnitude = properties.getDouble("mag");
                String place = properties.getString("place");
                long time = properties.getLong("time");

                data[i][0] = magnitude;
                data[i][1] = place;
                data[i][2] = new java.util.Date(time).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
}
