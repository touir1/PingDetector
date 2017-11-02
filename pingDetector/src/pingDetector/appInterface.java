package pingDetector;

import java.awt.EventQueue;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import java.awt.Font;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class appInterface {

	private static final Color RED = new Color(255, 0, 0);
	private static final Color YELLOW = new Color(255, 255, 0);
	private static final Color GREEN = new Color(0, 255, 0);

	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					appInterface window = new appInterface();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */

	public void changePingColor(Color color) {
		((JLabel) frame.getContentPane().getComponent(1)).setForeground(color);
	}

	public void reset() {
		changePingColor(GREEN);
		((JLabel) frame.getContentPane().getComponent(1)).setText("0");
		((JLabel) frame.getContentPane().getComponent(7)).setText("0");
		((JLabel) frame.getContentPane().getComponent(9)).setText("0");
	}

	class PingThread implements Runnable {

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void run() {
			LinkedList<Integer> pings = new LinkedList();
			final int INFINITE = 10000;

			String ip = "riot.de";
			// String pingResult = "";

			String pingCmd = "ping " + ip + " -t";
			try {
				Runtime r = Runtime.getRuntime();
				Process p = r.exec(pingCmd);

				BufferedReader in = new BufferedReader(new InputStreamReader(
						p.getInputStream()));

				String inputLine;
				int sumPingNow = 0;
				int pingNow = 0;
				int packets = -2;
				int loss = 0;
				while ((inputLine = in.readLine()) != null) {
					System.out.println(inputLine);
					packets++;
					if (packets > 0) {
						int pos = inputLine.indexOf("temps=");

						if (pos != -1) {
							String tmp = inputLine.substring(pos + 6);
							int ping = Integer.parseInt(tmp.substring(0,
									tmp.indexOf(" ")));
							if (pings.size() < 10) {
								pings.add(ping);
								sumPingNow += ping;
							} else {
								int first = pings.poll();
								sumPingNow -= first;
								pings.add(ping);
								sumPingNow += ping;
							}
						} else {
							loss++;
							if (pings.size() < 10) {
								pings.add(INFINITE);
								sumPingNow += INFINITE;
							} else {
								int first = pings.poll();
								sumPingNow -= first;
								pings.add(INFINITE);
								sumPingNow += INFINITE;
							}
						}
						pingNow = sumPingNow / pings.size();
						((JLabel) frame.getContentPane().getComponent(1))
								.setText(String.valueOf(pingNow));
						((JLabel) frame.getContentPane().getComponent(7))
								.setText(String.valueOf(loss));
						((JLabel) frame.getContentPane().getComponent(9))
								.setText(String.valueOf(packets));
						if (pingNow < 100) {
							changePingColor(GREEN);
						} else if (pingNow < 150) {
							changePingColor(YELLOW);
						} else {
							changePingColor(RED);
						}
					}
					// pingResult += inputLine;
				}
				in.close();

			} catch (IOException e) {
				System.out.println(e);
			}

		}

	}

	private PingThread ping = new PingThread();
	private Thread pingThread;

	private void startPing() {
		pingThread = new Thread(ping);
		pingThread.start();
	}

	@SuppressWarnings("deprecation")
	private void stopPing() {
		pingThread.stop();
	}

	public appInterface() {
		initialize();
		startPing();
		((JLabel) frame.getContentPane().getComponent(1)).setForeground(YELLOW);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setIconImage(new ImageIcon(appInterface.class.getResource("/pingDetector/wireless.ico")).getImage());
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		// element 0
		JLabel lblPingTester = new JLabel("Ping Tester");
		lblPingTester.setForeground(new Color(147, 112, 219));
		lblPingTester.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 20));
		lblPingTester.setBounds(158, 31, 131, 31);
		frame.getContentPane().add(lblPingTester);

		// element 1
		JLabel label = new JLabel("0");
		label.setFont(new Font("Tahoma", Font.BOLD, 40));
		label.setBounds(110, 80, 131, 49);
		label.setForeground(new Color(0, 255, 0));
		frame.getContentPane().add(label);
		// ((JLabel)frame.getContentPane().getComponent(1)).setForeground(YELLOW);

		// element 2
		JLabel lblMs = new JLabel("ms");
		lblMs.setFont(new Font("Tahoma", Font.PLAIN, 40));
		lblMs.setBounds(251, 73, 91, 63);
		frame.getContentPane().add(lblMs);

		// element 3
		JButton btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startPing();
			}
		});
		btnStart.setBounds(10, 213, 121, 37);
		frame.getContentPane().add(btnStart);

		// element 4
		JButton btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				stopPing();
			}
		});
		btnStop.setBounds(303, 213, 121, 37);
		frame.getContentPane().add(btnStop);

		// element 5
		JButton btnNewButton = new JButton("Reset");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopPing();
				reset();
			}
		});
		btnNewButton.setBounds(158, 213, 121, 37);
		frame.getContentPane().add(btnNewButton);

		// element 6
		JLabel lblPacketLoss = new JLabel("Packet Loss:");
		lblPacketLoss.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblPacketLoss.setBounds(10, 157, 150, 24);
		frame.getContentPane().add(lblPacketLoss);

		// element 7
		JLabel label_1 = new JLabel("0");
		label_1.setFont(new Font("Tahoma", Font.PLAIN, 20));
		label_1.setBounds(133, 157, 48, 24);
		frame.getContentPane().add(label_1);

		// element 8
		JLabel label_2 = new JLabel("/");
		label_2.setFont(new Font("Tahoma", Font.PLAIN, 20));
		label_2.setBounds(178, 158, 63, 23);
		frame.getContentPane().add(label_2);

		// element 9
		JLabel label_3 = new JLabel("0");
		label_3.setFont(new Font("Tahoma", Font.PLAIN, 20));
		label_3.setBounds(223, 160, 56, 19);
		frame.getContentPane().add(label_3);
	}
}
