package org.solovyev.math.dde;

import net.sourceforge.jeuclid.swing.JMathComponent;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * User: serso
 * Date: 2/13/12
 * Time: 4:40 PM
 */
public class MathMlViewer {

	private Component createComponents(@NotNull Input input) {
		/*
			 * An easy way to put space between a top-level container and its
			 * contents is to put the contents in a JPanel that has an "empty"
			 * border.
			 */
		JPanel pane = new JPanel();
		pane.setBackground(Color.WHITE);
		pane.setBorder(BorderFactory.createEmptyBorder(30, //top
				30, //left
				10, //bottom
				30) //right
		);
		pane.setLayout(new GridLayout(0, 1));

		for (String expression : input.getExpressions()) {
			final JMathComponent mathMlViewPort = new JMathComponent();
			mathMlViewPort.setContent(expression);
			mathMlViewPort.setFontSize(20);
			pane.add(mathMlViewPort);
		}

		return pane;
	}

	public static void show(@NotNull Input input) {
		//Create the top-level container and add contents to it.
		JFrame frame = new JFrame("MathMl Viewer");
		MathMlViewer app = new MathMlViewer();
		Component contents = app.createComponents(input);
		frame.getContentPane().add(contents, BorderLayout.CENTER);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

	public static class Input {

		@NotNull
		private final List<String> expressions;

		public Input(@NotNull List<String> expressions) {
			this.expressions = expressions;
		}

		@NotNull
		public List<String> getExpressions() {
			return expressions;
		}
	}
}


