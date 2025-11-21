package app;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import model.Comment;
import model.Post;
import store.DataStore;

public class MiniGardensApp {
    private JFrame frame;
    private JPanel feedPanel;
    private String username;
    private DataStore store;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new MiniGardensApp().start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public MiniGardensApp() {
        store = DataStore.getInstance();
    }

    private void start() {
        username = JOptionPane.showInputDialog(null, "Enter your username:", "Welcome", JOptionPane.PLAIN_MESSAGE);
        if (username == null || username.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Username required. Exiting.");
            System.exit(0);
        }
        createAndShowGUI();
    }

    private void createAndShowGUI() {
        frame = new JFrame("MiniGardens - Image & Comments");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 700);

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(8,8,8,8));
        JLabel userLabel = new JLabel("User: " + username);
        JButton uploadBtn = new JButton("Upload Image");
        uploadBtn.addActionListener(e -> showUploadDialog());

        top.add(userLabel, BorderLayout.WEST);
        top.add(uploadBtn, BorderLayout.EAST);

        feedPanel = new JPanel();
        feedPanel.setLayout(new BoxLayout(feedPanel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(feedPanel);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        frame.getContentPane().add(top, BorderLayout.NORTH);
        frame.getContentPane().add(scroll, BorderLayout.CENTER);

        refreshFeed();

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void showUploadDialog() {
        JTextField captionField = new JTextField();
        final JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int res = chooser.showOpenDialog(frame);
        if (res != JFileChooser.APPROVE_OPTION) return;
        File chosen = chooser.getSelectedFile();
        if (!isImageFile(chosen)) {
            JOptionPane.showMessageDialog(frame, "Please choose an image file (jpg/png/webp/gif).");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(frame, captionField, "Enter caption (optional)", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;
        String caption = captionField.getText();

        try {
            Files.createDirectories(Paths.get("uploads"));
            String newName = System.currentTimeMillis() + "_" + chosen.getName();
            Path target = Paths.get("uploads", newName);
            Files.copy(chosen.toPath(), target, StandardCopyOption.REPLACE_EXISTING);
            store.addPost(username, caption, newName);
            JOptionPane.showMessageDialog(frame, "Uploaded!");
            refreshFeed();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Upload failed: " + ex.getMessage());
        }
    }

    private boolean isImageFile(File f) {
        String name = f.getName().toLowerCase();
        return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
               name.endsWith(".png") || name.endsWith(".gif") || 
               name.endsWith(".webp");
    }

    private void refreshFeed() {
        feedPanel.removeAll();
        List<Post> posts = store.getPosts();
        SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy HH:mm");

        for (Post p : posts) {

            JPanel card = new JPanel(new BorderLayout());
            card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));

            JPanel info = new JPanel(new BorderLayout());
            info.setBorder(new EmptyBorder(4,4,4,4));
            JLabel meta = new JLabel("<html><b>" + p.getAuthor() + "</b> • " + fmt.format(p.getCreatedAt()) + "</html>");
            info.add(meta, BorderLayout.WEST);

            JLabel imgLabel = new JLabel();
            imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
            try {
                BufferedImage img = ImageIO.read(new File("uploads/" + p.getImageFilename()));
                int w = Math.min(800, img.getWidth());
                Image scaled = img.getScaledInstance(w, -1, Image.SCALE_SMOOTH);
                imgLabel.setIcon(new ImageIcon(scaled));
            } catch (Exception e) {
                imgLabel.setText("Image not found");
            }

            JTextArea captionArea = new JTextArea(p.getCaption());
            captionArea.setLineWrap(true);
            captionArea.setWrapStyleWord(true);
            captionArea.setEditable(false);
            captionArea.setBackground(null);

            DefaultListModel<String> cmListModel = new DefaultListModel<>();
            for (Comment c : p.getComments()) {
                cmListModel.addElement(c.getAuthor() + ": " + c.getText());
            }
            JList<String> cmList = new JList<>(cmListModel);
            cmList.setVisibleRowCount(3);

            JButton addComment = new JButton("Add Comment");
            addComment.addActionListener(e -> {
                String text = JOptionPane.showInputDialog(frame, "Enter comment:");
                if (text != null && !text.trim().isEmpty()) {
                    store.addComment(p.getId(), new Comment(username, text.trim()));
                    refreshFeed();
                }
            });

            card.add(info, BorderLayout.NORTH);
            card.add(imgLabel, BorderLayout.CENTER);

            JPanel south = new JPanel(new BorderLayout());
            south.setBorder(new EmptyBorder(6,6,6,6));
            south.add(captionArea, BorderLayout.NORTH);

            JPanel commentPanel = new JPanel(new BorderLayout());
            commentPanel.add(new JScrollPane(cmList), BorderLayout.CENTER);
            commentPanel.add(addComment, BorderLayout.SOUTH);
            south.add(commentPanel, BorderLayout.CENTER);

            card.add(south, BorderLayout.SOUTH);

            feedPanel.add(Box.createRigidArea(new Dimension(0,8)));
            feedPanel.add(card);
        }

        feedPanel.revalidate();
        feedPanel.repaint();
    }
}

