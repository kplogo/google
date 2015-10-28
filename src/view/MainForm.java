package view;

import model.DatabaseCollection;
import model.Document;
import model.Result;
import model.Term;
import model.document.Method;
import service.DocumentParser;
import service.KeywordParser;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainForm {
	private JPanel panel1;
	private JTextField database;
	private JButton loadDatabase;
	private JTextField query;
	private JTextField keywords;
	private JList<Result> results;
	private JButton loadKeyword;
	private JButton search;
	private JComboBox method;
	private JComboBox keywordsEnabled;
	private JButton oznacz;
	private JButton newQuestion;
	private JSpinner alfa;
	private JSpinner beta;
	private JSpinner gamma;
	private JTextField queryHelp;
	private static boolean isKeywordsEnabled;
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
		alfa.setValue(1);
		beta.setValue(1);
		gamma.setValue(1);
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
			isKeywordsEnabled = keywordsEnabled.getSelectedIndex() != 0;
			DatabaseCollection.clear();
			if (isKeywordsEnabled) {
				new KeywordParser(keywords.getText()).parse();
			}
			new DocumentParser(database.getText()).parse();
			Method method1 = Method.valueOf((String) MainForm.this.method.getSelectedItem());
			Document query1 = new Document(MainForm.this.query.getText(), "", false);
			showResults(method1, query1);
		});
		oznacz.addActionListener(e -> {
			for (Result result : results.getSelectedValuesList()) {
				result.setMarkedAsGood(!result.isMarkedAsGood());
			}
			results.repaint();
		});
		newQuestion.addActionListener(e -> {
			ResultModel model = (ResultModel) results.getModel();
			Document query1 = model.getQuery();
			calculateRelevance(model.getResultList(), query1, (Double) alfa.getValue(), (Double) beta.getValue(), (Double) gamma.getValue());
			queryHelp.setText(query1.getQueryText());
			Method method1 = Method.valueOf((String) MainForm.this.method.getSelectedItem());
			showResults(method1, query1);
		});
	}

	private void showResults(Method method1, Document query1) {
		List<Result> querySimilarity = getQuerySimilarity(method1, query1);
		results.setModel(new ResultModel(querySimilarity, query1));
		results.addMouseListener(new ListClickListener(results));
	}

	private void calculateRelevance(List<Result> resultList, Document query, double alfa, double beta, double gama) {
		List<Document> goodDocuments = new ArrayList<>();
		List<Document> badDocuments = new ArrayList<>();
		for (Result result : resultList) {
			if (result.isMarkedAsGood()) {
				goodDocuments.add(result.getDocument());
			} else {
				badDocuments.add(result.getDocument());

			}
		}
		for (Term term : DatabaseCollection.getTermMap().values()) {
			double relevance = alfa * query.getTermRelevance(term) + (avg(term, goodDocuments)) * beta - avg(term, badDocuments) * gama;
			query.setTermRelevance(term, relevance);
		}
	}

	private double avg(Term term, List<Document> documents) {
		double count = 0;
		if (documents == null || documents.isEmpty()) {
			return 0;
		}
		for (Document document : documents) {
			count += document.getTermCount(term);
		}
		return count / documents.size();
	}

	private List<Result> getQuerySimilarity(Method method, Document query) {
		List<Result> resultList = new ArrayList<>();
		List<Result> list = new ArrayList<>();
		for (Document document : DatabaseCollection.getDocumentList()) {
			double similarity = document.similarity(query, method);
			if (similarity != -1) {
				list.add(new Result(document, similarity));
			}
		}
		list.stream().sorted((o1, o2) -> {
			double diff = o1.getSimilarity() - o2.getSimilarity();
			if (diff < 0) {
				return 1;
			} else if (diff > 0) {
				return -1;
			}
			return 0;
		}).limit(5).forEach(resultList::add);
		return resultList;
	}

	public static boolean isKeywordsEnabled() {
		return isKeywordsEnabled;
	}


	private class ListClickListener implements MouseListener {
		private JList<Result> results;

		public ListClickListener(JList<Result> results) {

			this.results = results;
		}


		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.getClickCount() > 1) {
				Result selectedValue = results.getSelectedValue();
				selectedValue.setMarkedAsGood(!selectedValue.isMarkedAsGood());
				results.repaint();
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}
	}

	private class ResultModel implements ListModel<Result> {
		private List<Result> resultList;
		private Document query;

		private ResultModel(List<Result> resultList, Document query) {
			this.resultList = resultList;
			this.query = query;
		}

		public List<Result> getResultList() {
			return resultList;
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

		public Document getQuery() {
			return query;
		}
	}
}
