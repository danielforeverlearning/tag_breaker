



import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class tag_breaker {
	
	//(1) when you encounter '<' then you are inside tag
	//(2) when you encounter '>' then you are outside tag
	
	//(3) Get 2 tags
	//    (a.) if begin and end tag (for example <id> ..... </id>), then print whole thing on 1 line, reset save_first_tag and save_between_tag and save_second_tag, state becomes SEARCH_FIRST_TAG_START
	//    (b.) if end and end tag (for example </cvalue></cvalues>), then print end of 1st tag on 1 line, and new 1st tag == save_between_tags + old 2nd tag, state becomes SEARCH_SECOND_TAG_START
	//    (c.) if begin and begin tag (for example <parameters><parameter>), then print end of 1st tag on 1 line, and new 1st tag == save_between_tags + old 2nd tag, state becomes SEARCH_SECOND_TAG_START
	//    (d.) if end and begin tag (for example </cvalue><cvalue> ), then print end of 1st tag on 1 line, and new 1st tag == save_between_tags + old 2nd tag, state becomes SEARCH_SECOND_TAG_START
	
	
	private enum STATE {
			SEARCH_FIRST_TAG_START,  //'<'
			SEARCH_FIRST_TAG_END,    //'>'
			SEARCH_SECOND_TAG_START, //'<'
			SEARCH_SECOND_TAG_END    //'>'
		}
	
	public static void main(String[] args) throws Exception {
		
		
		boolean first_tag_is_end_tag           = false;
		boolean second_tag_is_end_tag          = false;
		boolean check_char_after_1st_tag_start = false;
		boolean check_char_after_2nd_tag_start = false;
		
		STATE mystate = STATE.SEARCH_FIRST_TAG_START;
		String save_first_tag = "";
		String save_between_tags = "";
		String save_second_tag = "";
		
		PrintWriter mywriter = new PrintWriter("tag_breaker_output.txt");
		FileReader fileread = new FileReader("Write_Document_Refresh_Parameters_output.txt");
		BufferedReader in = new BufferedReader(fileread);
		
		String myline = in.readLine();
		while (myline != null) {
			
			//got a line	
			for (int cc=0; cc < myline.length(); cc++) {
				
				char mychar = myline.charAt(cc);
				
				if (mystate == STATE.SEARCH_FIRST_TAG_START) {
					if (mychar == '<') {
						mystate = STATE.SEARCH_FIRST_TAG_END; //now inside first tag
						save_first_tag += mychar;
						check_char_after_1st_tag_start = true;
					}
					else
						save_first_tag += mychar;
				}
				else if (mystate == STATE.SEARCH_FIRST_TAG_END) {
					
					if (check_char_after_1st_tag_start) {
						if (mychar == '/')
							first_tag_is_end_tag = true;
						else
							first_tag_is_end_tag = false;
						check_char_after_1st_tag_start = false;
					}
					
					if (mychar == '>') {
						mystate = STATE.SEARCH_SECOND_TAG_START; //now finished first tag
						save_first_tag += mychar;
					}
					else
						save_first_tag += mychar;
				}
				else if (mystate == STATE.SEARCH_SECOND_TAG_START) {
					if (mychar == '<') {
						mystate = STATE.SEARCH_SECOND_TAG_END;
						save_second_tag += mychar;
						check_char_after_2nd_tag_start = true;
					}
					else
						save_between_tags += mychar;
				}
				else if (mystate == STATE.SEARCH_SECOND_TAG_END) {
					
					if (check_char_after_2nd_tag_start) {
						if (mychar == '/')
							second_tag_is_end_tag = true;
						else
							second_tag_is_end_tag = false;
						check_char_after_2nd_tag_start = false;
					}
					
					if (mychar != '>')
						save_second_tag += mychar;
					
					else { //mychar == '>'
						
						save_second_tag += mychar;
						
						//(3) Got 2 tags
						//    (a.) if begin and end tag (for example <id> ..... </id>), then print whole thing on 1 line, reset save_first_tag and save_between_tag and save_second_tag, state becomes SEARCH_FIRST_TAG_START
						//    (b.) else print end of 1st tag on 1 line, and new 1st tag == save_between_tags + old 2nd tag, new first_tag_is_end_tag = old second_tag_is_end_tag, reset save_between_tags and save_second_tag, state becomes SEARCH_SECOND_TAG_START
						
						if ((first_tag_is_end_tag == false) && second_tag_is_end_tag) { //(3a.)
							mywriter.print(save_first_tag);
							mywriter.print(save_between_tags);
							mywriter.print(save_second_tag);
							mywriter.println();
							
							save_first_tag = "";
							save_between_tags = "";
							save_second_tag = "";
							
							mystate = STATE.SEARCH_FIRST_TAG_START;
						}
						else { //(3b.)
							mywriter.print(save_first_tag);
							mywriter.println();
							
							first_tag_is_end_tag = second_tag_is_end_tag;
							save_first_tag = save_between_tags + save_second_tag;
							save_between_tags = "";
							save_second_tag = "";
							
							mystate = STATE.SEARCH_SECOND_TAG_START;
						}
					}
				}
			}//for
			
			myline = in.readLine();
		}//while myline
		
		mywriter.print(save_first_tag);
		mywriter.print(save_between_tags);
		mywriter.print(save_second_tag);
		mywriter.println();
		
		mywriter.close();
		in.close();
		fileread.close();
		System.out.println("DONE");
	}//main
	
}//class
