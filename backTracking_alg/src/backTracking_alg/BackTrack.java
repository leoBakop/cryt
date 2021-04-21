package backTracking_alg;

import java.util.ArrayList;

public class BackTrack {
	Puzzle puzzle;
	ArrayList<Letter> letters;
	ArrayList<Constrait> constarits;
	ArrayList<Integer> unusedDigits;

	public BackTrack(Puzzle puzzle) {
		this.puzzle = puzzle;
		this.unusedDigits=new ArrayList<>();
		for(int i=1; i<=puzzle.numberSystem; i++) 
			this.unusedDigits.add(i);
		this.letters=new ArrayList<>();
		this.fillLetters();
		this.constarits=new ArrayList<>();
		this.createConstraits();
		boolean dc=this.checkForDoubleConstraits();
		this.upgradeLetters(dc);
		
		

	}


	public static void main(String[] args) {
		Puzzle puzzle= new Puzzle(args[0], args[1], args[2], Integer.parseInt(args[3]));
		BackTrack bt=new BackTrack(puzzle);
		bt.ExhaustiveSolve(bt.unusedDigits);
		bt.printSolution();
	}

	//initializations methods

	void fillLetters() {
		for(int i=0; i<puzzle.first.length(); i++) {
			if(this.searchInLetterArrayList(this.letters,puzzle.first.charAt(i))==null)
				letters.add(new Letter(puzzle.first.charAt(i), -1,false));
		}

		for(int i=0; i<puzzle.second.length(); i++) {
			if(this.searchInLetterArrayList(this.letters,puzzle.second.charAt(i))==null)
				letters.add(new Letter(puzzle.second.charAt(i), -1, false));
		}

		for(int i=0; i<puzzle.result.length(); i++) {
			if(this.searchInLetterArrayList(this.letters,puzzle.result.charAt(i))==null)
				letters.add(new Letter(puzzle.result.charAt(i), -1, true));
		}		
	}

	void createConstraits() {

		String first=this.puzzle.first;
		String result=this.puzzle.result;
		int resultLength=result.length()-1;
		for(int i=first.length()-1; i>=0; i--) {
			this.constarits.add(new Constrait(first.charAt(i), result.charAt(resultLength)));
			resultLength--;
		}
		if(result.length()>first.length()) {//ex to+to=for
			Letter letter=this.searchInLetterArrayList(this.letters,result.charAt(0));
			letter.symbol=1;
			Object digit=(Object)this.searchInUnusedDigits(unusedDigits, letter.symbol);
			this.unusedDigits.remove((Object)digit);
			this.letters.remove(letter);
			this.letters.add(letter);
		}
	}
	
	boolean checkForDoubleConstraits() {
		for(Constrait con : this.constarits) {
			if(con.a==con.b) {
				Letter let=this.searchInLetterArrayList(letters, con.a);
				let.symbol=0;
				this.letters.remove(let);
				this.letters.add(let);
				return true;
			}
		}
		return false;
	}
	
	void upgradeLetters(boolean dConstr) {
		if(dConstr) //if there are double constraints then 0 is used for them 
			return;
		for(Letter l :this.letters) { // 0 is not used for double constraint so you have to use it
			if(l.symbol==1) {
				l.symbol=0;
				this.unusedDigits.add(0,1);
				this.letters.remove(l);
				this.letters.add(l);
			}
		}
	}

	//search methods

	Letter searchInLetterArrayList(ArrayList<Letter> letters, char letter) {
		for(int i=0; i<letters.size(); i++) {
			if (letter==letters.get(i).letter)
				return letters.get(i);
		}
		return null;
	}

	boolean letterArrayIsEmpty(ArrayList<Letter> letters) {
		for(Letter let : letters) {
			if(let.symbol==-1) //if there is at least one letter not assigned then you haven't assigned all the letters
				return false;
		}
		return true;
	}

	Letter nextLetter(ArrayList<Letter> l) {
		for(Letter letter : l) { //return sth that is not result
			if(letter.symbol==-1 && !letter.fromResult) {
				return letter;
			}
		}
		for(Letter let : l) {
			if(let.symbol==-1 ) {
				return let;
			}
		}
		return null;
	}

	int searchInUnusedDigits(ArrayList<Integer> unusedDig, int dig) {
		for(int i=0; i<unusedDig.size(); i++) {
			if(unusedDig.get(i)==dig)
				return unusedDig.get(i);
		}

		return -1;

	}

	boolean checkForConstrait(ArrayList<Letter> letters,Letter let) {

		for(Constrait con : this.constarits) {
			if( let.letter==con.a) {
				Letter b=this.searchInLetterArrayList(letters, con.b); //if let is the a in constraint the search for b
				if(b.symbol==-1) {
					b.symbol=2*let.symbol;
					letters.remove(b);
					letters.add(b);
					return true;
				}
				//if b.symbol!=-1
				if(b.symbol==2*let.symbol) 
					return true;
				return false;
			}

			if(let.letter==con.b) {
				Letter a=this.searchInLetterArrayList(letters, con.a); //search for a
				if(a.symbol*2==let.symbol)
					return true;
			return false;
			}
				
		}
		//constraint with this letter has not been found
		//possible error
		System.out.println("possible error");
		return false;
	}


	//main method

	ArrayList<Letter> ExhaustiveSolve ( ArrayList<Integer> unusedDigits) {

		if(letterArrayIsEmpty(letters))
			return letters;


		for (int i=0; i<unusedDigits.size(); i++) {
			int digit=unusedDigits.get(i);
			Letter let=this.nextLetter(letters);
			if(let==null) {
				System.out.println("unexpected error");
				return null;
			}
			letters.remove(let);
			

			let.symbol=digit;
			letters.add(let);
			if(this.checkForConstrait(letters, let)) {
				unusedDigits.remove((Object)digit);
				this.ExhaustiveSolve(unusedDigits);
				return letters;
			}
			//unusedDigits.add(digit);
			letters.remove(let);
			let.symbol=-1;
			letters.add(let);
		}
		return null;
	}
	
	void printSolution() {
		int a=0;
		for(Letter l :this.letters) {
			System.out.println(l.letter+"->"+l.symbol);
			if(l.symbol==-1)
				a=-1;
		}
		
		if(a==-1)
			System.out.println("solution not found");
		else
			System.out.println("that is a right solution");
	}

}
