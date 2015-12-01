package view;

import model.DatabaseCollection;
import model.Document;
import model.Result;
import model.Term;
import model.document.Method;
import service.DocumentParser;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class MainForm {
	private JPanel panel1;
	private JTextField database;
	private JButton loadDatabase;
	private JTextField query;
	private JList<Result> results;
	private JButton search;
	private JComboBox method;
	private JButton oznacz;
	private JButton newQuestion;
	private JSpinner alfa;
	private JSpinner beta;
	private JSpinner gamma;
	private JTextField queryHelp;
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
		loadDatabase.addActionListener(e -> openFile(database));
		search.addActionListener(e -> search());
		oznacz.addActionListener(e -> markAsImportant());
		newQuestion.addActionListener(e -> repairQuestion());
	}

	private void repairQuestion() {
		ResultModel model = (ResultModel) results.getModel();
		Document query1 = model.getQuery();
		calculateRelevance(model.getResultList(), query1, (Integer) alfa.getValue(), (Integer) beta.getValue(), (Integer) gamma.getValue());
		queryHelp.setText(query1.getQueryText());
		Method method1 = Method.valueOf((String) MainForm.this.method.getSelectedItem());
		showResults(method1, query1);
	}

	private void markAsImportant() {
		for (Result result : results.getSelectedValuesList()) {
			result.setMarkedAsGood(!result.isMarkedAsGood());
		}
		results.repaint();
	}

	private void search() {
		DatabaseCollection.clear();
		new DocumentParser(database.getText()).parse();
		logTerms();
		logSiblings();
		Method method1 = Method.valueOf((String) MainForm.this.method.getSelectedItem());
		Document query1 = new Document(MainForm.this.query.getText(), "", false);
		showResults(method1, query1);
	}

	private void openFile(JTextField keywords) {

		fileChooser.setCurrentDirectory(new File(keywords.getText()));
		int returnVal = fileChooser.showOpenDialog(panel1);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			keywords.setText(file.getPath());
		}
	}

	private void logTerms() {
		DatabaseCollection.getTermMap().entrySet().stream()
				.filter(termEntry -> !Objects.equals(termEntry.getValue().getSiblingsInfo(), ""))
				.forEach(termEntry -> System.out.println(termEntry.getValue().getSiblingsInfo()));
	}


	private void logSiblings() {
		DatabaseCollection.getSiblings().values().stream()
				.sorted((o1, o2) -> o2.sum() - o1.sum())
				.filter(siblings -> siblings.sum()>=getMaxCount()-getMaxCount()/10.0)
				.forEach(termEntry -> System.out.println(termEntry.toString()));
	}

	private int getMaxCount() {
		return  DatabaseCollection.getSiblings().values().stream()
				.sorted((o1, o2) -> o2.sum() - o1.sum())
				.max((s1,s2)-> s1.sum()-s2.sum()).get().sum();
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
		list.stream().sorted().limit(5).forEach(resultList::add);
		return resultList;
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
