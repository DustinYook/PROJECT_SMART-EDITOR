package editor;

import java.util.ArrayList;

public class BruteForce 
{
	private String text;
	private String pattern;
	private ArrayList<Integer> arr = new ArrayList<Integer>();

	public BruteForce(String text, String pattern) 
	{
		this.text = text;
		this.pattern = pattern;
	}

	public ArrayList<Integer> patternMatch(String text, String pattern) 
	{
		int counter = 0;
		int start = -1;

		for (int i = 0; i < text.length() - pattern.length() + 1; i++) 
		{
			for (int j = 0; j < pattern.length(); j++) 
			{
				if (text.charAt(i + j) != pattern.charAt(j)) 
				{
					counter = 0;

					if (i != text.length() - pattern.length())
						break;
					arr.add(Integer.valueOf(-1));
					return arr;
				}
				counter++;

				if (counter == 1)
					start = i;
				if (j == pattern.length() - 1)
					arr.add(Integer.valueOf(start));
			}
		}
		return arr;
	}
}