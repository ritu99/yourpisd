/**
 * This file is part of yourPISD.
 *
 *  yourPISD is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  yourPISD is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with yourPISD.  If not, see <http://www.gnu.org/licenses/>.
 */

package app.sunstreak.yourpisd;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TermFinder {

	// TODO Hardcoded for 2015-2016 school year
	public enum Term {
		TERM_0 ("1st Six Weeks", "8/24/2015","9/29/2015"),
		TERM_1 ("2nd Six Weeks", "9/29/2015", "11/5/2015"),
		TERM_2 ("3rd Six Weeks", "11/5/2015", "12/18/2015"),
		TERM_3 ("1st Semester Exam", "12/18/2015", "1/5/2016"),
		TERM_4 ("4th Six Weeks", "1/5/2016", "2/19/2016"),
		TERM_5 ("5th Six Weeks", "2/19/2016", "4/15/2016"),
		TERM_6 ("6th Six Weeks", "4/15/2016", "6/3/2016"),
		TERM_7 ("2nd Semester Exam", "6/3/2016", "6/3/2016");
		
		public final String name;
		public Date startDate;
		public Date endDate;

		private Term (String name, String startDate, String endDate) {
			this.name = name;
			try {
				this.startDate = dfm.parse(startDate);
				this.endDate = dfm.parse(endDate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static final SimpleDateFormat dfm = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

	public static int getCurrentTermIndex() {
		
		Date d = Calendar.getInstance().getTime();
		
		for (int i = 0; i < Term.values().length; i++) {
			if (d.compareTo(Term.values()[i].startDate) >= 0 && d.compareTo(Term.values()[i].endDate) <= 0)
				return i;
		}
		
		return 0;
	}
	
}
