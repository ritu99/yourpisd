package app.sunstreak.yourpisd;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout.Alignment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.ImageSpan;
import android.text.style.ScaleXSpan;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import app.sunstreak.yourpisd.net.DataGrabber;
import app.sunstreak.yourpisd.net.DateHandler;


@SuppressLint("ValidFragment")
public class ClassSwipeActivity extends FragmentActivity {
	static List<Fragment> mFragments;
	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	static ViewPager mViewPager;

	static int studentIndex;
	static int receivedClassIndex;
	static int classCount;
	static int classesMade = 0;
	static int termIndex;
	static boolean doneMakingClasses;

	static DataGrabber dg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_class_swipe);



		receivedClassIndex = getIntent().getExtras().getInt("classIndex");
		classCount = getIntent().getExtras().getInt("classCount");
		termIndex = getIntent().getExtras().getInt("termIndex");
		studentIndex = getIntent().getExtras().getInt("studentIndex");

		setTitle(TermFinder.Term.values()[termIndex].name);

		dg = ((DataGrabber) getApplication() );

		dg.studentIndex = studentIndex;

		mFragments = new ArrayList<Fragment>();
		for (int i = 0; i < classCount; i++) {
			Bundle args = new Bundle();
			args.putInt(DescriptionFragment.ARG_SECTION_NUMBER, i);
			Fragment fragment = new DescriptionFragment();
			fragment.setArguments(args);
			mFragments.add(fragment);
		}



		//		System.out.println("mFragments size = " + mFragments.size() + " and classCount = " + classCount);


		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager(), mFragments);


		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
		mViewPager.setCurrentItem(receivedClassIndex);
		mViewPager.setOffscreenPageLimit(7);


	}

	@Override
	protected void onPause() {
		super.onPause();

	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.class_swipe_actions, menu);


		if (termIndex == 0)
			menu.findItem(R.id.previous_six_weeks).setEnabled(false);
		else if (termIndex == 3)
			menu.findItem(R.id.next_six_weeks).setEnabled(false);

		// Create list of students in Menu.
		if (dg.MULTIPLE_STUDENTS) {
			for (int i = 0; i < dg.getStudents().size(); i++) {
				String name = dg.getStudents().get(i).name;
				MenuItem item = menu.add(name);

				// Set the currently enabled student un-clickable.
				if (i == studentIndex)
					item.setEnabled(false);

				item.setOnMenuItemClickListener(new StudentSelectListener(i));
				item.setVisible(true);
			}
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		Intent intent;
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.log_out:
			SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
			editor.putBoolean("auto_login", false);
			editor.commit();
			intent = new Intent(this, LoginActivity.class);
			// Clear all activities between this and LoginActivity
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			return true;
			/*
		case R.id.refresh:
			Intent refreshIntent = new Intent(this, LoginActivity.class);
			refreshIntent.putExtra("Refresh", true);
			startActivity(refreshIntent);
			finish();
			return true;
			 */
		case R.id.previous_six_weeks:
			intent = new Intent(this, ClassSwipeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("studentIndex", studentIndex);
			intent.putExtra("classCount", classCount);
			intent.putExtra("classIndex", mViewPager.getCurrentItem());
			// Don't go into the negatives!
			intent.putExtra("termIndex", termIndex - 1 >= 0 ? termIndex - 1 : 0);
			startActivity(intent);

			//			overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);

			return true;
		case R.id.next_six_weeks:
			intent = new Intent(this, ClassSwipeActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("studentIndex", studentIndex);
			intent.putExtra("classCount", classCount);
			intent.putExtra("classIndex", mViewPager.getCurrentItem());
			// Don't go too positive!
			intent.putExtra("termIndex", termIndex + 1 <= 7 ? termIndex + 1 : 7);
			startActivity(intent);
			//			overridePendingTransition(R.anim.slide_in_up, R.anim.slide_out_down);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		List<Fragment> fragmentList;

		public SectionsPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
			super(fm);
			fragmentList = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = fragmentList.get(position);        
			return fragment;
		}

		@Override
		public int getCount() {
			return fragmentList.size();
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return dg.getCurrentStudent().getClassName(dg.getCurrentStudent().getClassMatch()[position]);
		}
	}


	/**
	 * A fragment that displays grades for one class.
	 */
	public static class DescriptionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";
		private ClassGradeTask mClassGradeTask;
		private int position;
		private int classIndex;
		private JSONObject mClassGrade;
		private View rootView;





		public DescriptionFragment() {

		}




		@Override
		public void onPause () {
			if (mClassGradeTask != null)
				mClassGradeTask.cancel(true);
			super.onPause();
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)  {

			position = getArguments().getInt(ARG_SECTION_NUMBER);
			classIndex = dg.getCurrentStudent().getClassMatch()[position];

			rootView = inflater.inflate(R.layout.class_description, container, false);

			if ( dg.getCurrentStudent().hasClassGrade(classIndex, termIndex) ) {
				System.out.println(dg.studentIndex + " " + classIndex + " " + termIndex);
				System.out.println(dg.getCurrentStudent().name);
				mClassGrade = dg.getCurrentStudent().getClassGrade(classIndex, termIndex);
				System.out.println(mClassGrade);
				setUiElements();
			} else {
				mClassGradeTask = new ClassGradeTask();
				mClassGradeTask.execute(classIndex, termIndex);
			}


			return rootView;
		}



		@SuppressLint("ResourceAsColor")
		public class ClassGradeTask extends AsyncTask<Integer, Void, JSONObject> {

			@Override
			protected void onPreExecute () {
				System.out.println("ClassGradeTask starting");
			}

			@Override
			protected void onPostExecute (final JSONObject result) {
				mClassGrade = result;
				System.out.println("ClassGradeTask finished");

				setUiElements();
			}

			@Override
			protected JSONObject doInBackground(Integer... integers) {
				try {
					return dg.getCurrentStudent().getClassGrade(integers[0], integers[1]);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}

		public void setUiElements () {
			int lastIdAdded = R.id.teacher_name;
			TextView teacher = (TextView) rootView.findViewById(R.id.teacher_name);
			TextView sixWeeksAverage = (TextView) rootView.findViewById(R.id.six_weeks_average);
			LinearLayout desc = (LinearLayout) rootView.findViewById(R.id.descriptions);
			teacher.setVisibility(View.VISIBLE);
			sixWeeksAverage.setVisibility(View.VISIBLE);
//			desc.setVisibility(View.VISIBLE);
//			LinearLayout classDescriptionLinearLayout = (LinearLayout) rootView.findViewById(R.id.class_description_linear_layout);
			int id = lastIdAdded;
			
			
			try {
				// The following line prevents force close. Idk why.
				// Maybe the extra print time somehow fixes it...
				System.out.println(mClassGrade);

				teacher.setText(dg.getCurrentStudent().getClassList().getJSONObject(classIndex).optString("teacher"));

				int avg = mClassGrade.optInt("average", -1);
				if (avg != -1) {
					String average = avg + "";
					sixWeeksAverage.setText(average);
				} else
					sixWeeksAverage.setVisibility(TextView.INVISIBLE);





				// Add current student's name
				if (dg.MULTIPLE_STUDENTS) {
					LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					LinearLayout studentName = (LinearLayout) inflater.inflate(R.layout.main_student_name_if_multiple_students, desc, false);
					((TextView) studentName.findViewById(R.id.name)).setText(dg.getCurrentStudent().name);
					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lp.addRule(RelativeLayout.BELOW, lastIdAdded);
					lastIdAdded = R.id.name;
					desc.addView(studentName);
					id = lastIdAdded;
				}
				for (int category = 0;
						category < mClassGrade.getJSONArray("categoryGrades").length();
						category++)
				{
					RelativeLayout card = new RelativeLayout(getActivity());
					card.setBackgroundResource(R.drawable.dropshadow);

					LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					
					// Name of the category ("Daily Work", etc)
					String categoryName = mClassGrade.getJSONArray("categoryGrades").getJSONObject(category).getString("Category");

//					int layoutCounter = 0;

					// for every grade in this term [any category]
					for(int i = 0; i< mClassGrade.getJSONArray("grades").length(); i++)
					{
						// only if this grade is in the category which we're looking for
						if (mClassGrade.getJSONArray("grades").getJSONObject(i).getString("Category")
								.equals(categoryName))
						{
							LinearLayout innerLayout = (LinearLayout) inflater.inflate(R.layout.class_swipe_grade_view, desc, false);
							
							
							TextView descriptionView = (TextView) innerLayout.findViewById(R.id.description);
							String description = "" + mClassGrade.getJSONArray("grades")
									.getJSONObject(i).getString("Description");
							descriptionView.setText(description);

							TextView grade = (TextView) innerLayout.findViewById(R.id.grade);
							String gradeValue = mClassGrade.getJSONArray("grades")
									.getJSONObject(i).optString("Grade", "") + "";
							grade.setText(gradeValue);
							
							RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
									RelativeLayout.LayoutParams.MATCH_PARENT,
									RelativeLayout.LayoutParams.WRAP_CONTENT);
							lp.addRule(RelativeLayout.BELOW, lastIdAdded);

							// Add the view to the RelativeLayout
							innerLayout.setId(lastIdAdded + 1);
//							desc.addView(innerLayout, lp);
							card.addView(innerLayout, lp);
							lastIdAdded++;
						}

					}
					// Create a category summary view
					LinearLayout categoryLayout = (LinearLayout) inflater.inflate(R.layout.class_swipe_category_card, desc, false);
					
					TextView categoryNameView = (TextView) categoryLayout.findViewById(R.id.category_name);
					categoryNameView.setText(categoryName);

					TextView scoreView = (TextView) categoryLayout.findViewById(R.id.category_score);
					String categoryScore = mClassGrade.getJSONArray("categoryGrades")
							.getJSONObject(category).optString("Letter", "") + "";
					scoreView.setText(categoryScore);

					RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
							RelativeLayout.LayoutParams.MATCH_PARENT,
							RelativeLayout.LayoutParams.WRAP_CONTENT);
					lp.addRule(RelativeLayout.BELOW, lastIdAdded);
					
					// Add the view to the RelativeLayout
					categoryLayout.setId(lastIdAdded + 1);
					card.addView(categoryLayout, lp);
//					desc.addView(categoryLayout, lp);
					lastIdAdded++;
					
					LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.MATCH_PARENT,
							LinearLayout.LayoutParams.WRAP_CONTENT);
//					lp1.addRule(RelativeLayout.BELOW, id);
					desc.addView(card, lp1);
					Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_in_right);
					animation.setStartOffset(0);
					card.startAnimation(animation);
					id = lastIdAdded;
				}


//				TextView lastUpdatedView = new TextView(getActivity());
//				lastUpdatedView.setText(DateHandler.timeSince(mClassGrade.getLong("lastUpdated")));
//				lastUpdatedView.setPadding(10, 0, 0, 0);
//				RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//						RelativeLayout.LayoutParams.WRAP_CONTENT,
//						RelativeLayout.LayoutParams.WRAP_CONTENT);
//				lp.addRule(RelativeLayout.BELOW, lastIdAdded);
//				desc.addView(lastUpdatedView, lp);


			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}


	class StudentSelectListener implements MenuItem.OnMenuItemClickListener {

		int menuStudentIndex;

		public StudentSelectListener (int menuStudentIndex) {
			this.menuStudentIndex = menuStudentIndex;
		}

		public boolean onMenuItemClick(MenuItem arg0) {

			dg.studentIndex = menuStudentIndex;

			Intent intent = new Intent(ClassSwipeActivity.this, MainActivity.class);
			intent.putExtra("mainActivitySection", 1);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

			startActivity(intent);

			//	        	overridePendingTransition(R.anim.slide_in_down, R.anim.slide_out_up);
			return true;
			//			}

		}

	}
	
	private class id {
		public static final int category_card = 1290415;
	}

}
