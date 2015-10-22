package view;

import model.DatabaseCollection;
import model.Document;
import model.Result;
import model.Term;
import service.DocumentParser;
import service.KeywordParser;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.io.File;
import java.util.*;

public class MainForm {
	public static final boolean ONLY_KEYWORDS_DISABLED = true;
	private JPanel panel1;
	private JTextField database;
	private JButton loadDatabase;
	private JTextField query;
	private JTextField keywords;
	private JList<Result> results;
	private JButton loadKeyword;
	private JButton search;
	private JComboBox method;
	private JFileChooser fileChooser = new JFileChooser();

	//information retrieval

	public static void main(String[] args) {
		JFrame frame = new JFrame("MainForm");
		frame.setContentPane(new MainForm().panel1);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public MainForm() {
		loadDatabase.addActionListener(e -> {
			fileChooser.setCurrentDirectory(new File(database.getText()));
			int returnVal = fileChooser.showOpenDialog(panel1);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				database.setText(file.getPath());
			}
		});
		loadKeyword.addActionListener(e -> {
			fileChooser.setCurrentDirectory(new File(keywords.getText()));
			int returnVal = fileChooser.showOpenDialog(panel1);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fileChooser.getSelectedFile();
				keywords.setText(file.getPath());
			}
		});
		search.addActionListener(e -> {
			DatabaseCollection.clear();
			new KeywordParser(keywords.getText());
			new DocumentParser(database.getText());
			logTerms();
			Document.Method method1 = Document.Method.valueOf((String) MainForm.this.method.getSelectedItem());
			Document query1 = new Document(MainForm.this.query.getText(), "", false);
			ResultModel model = new ResultModel(getQuerySimilarity(method1, query1));
			results.setModel(model);
		});
	}

	private void logTerms() {
		DatabaseCollection.getTermMap().entrySet().stream()
				.filter(termEntry -> !Objects.equals(termEntry.getValue().getSiblingsInfo(), ""))
				.forEach(termEntry -> System.out.println(termEntry.getValue().getSiblingsInfo()));
	}

	private List<Result> getQuerySimilarity(Document.Method method, Document query) {
		List<Result> resultList = new ArrayList<>();
		for (Document document : DatabaseCollection.getDocumentList()) {
			double similarity = document.similarity(query, method);
			if (similarity != -1) {
				resultList.add(new Result(document, similarity));
			}
		}
		Collections.sort(resultList, (o1, o2) -> {
			double diff = o1.getSimilarity() - o2.getSimilarity();
			if (diff < 0) {
				return 1;
			} else if (diff > 0) {
				return -1;
			}
			return 0;
		});
		return resultList;
	}

	private class ResultModel implements ListModel<Result> {
		private List<Result> resultList;

		private ResultModel(List<Result> resultList) {
			this.resultList = resultList;
		}

		@Override
		public int getSize() {
			return resultList.size();
		}

		@Override
		public Result getElementAt(int index) {
			return resultList.get(index);
		}

		@Override
		public void addListDataListener(ListDataListener l) {

		}

		@Override
		public void removeListDataListener(ListDataListener l) {

		}
	}
}
