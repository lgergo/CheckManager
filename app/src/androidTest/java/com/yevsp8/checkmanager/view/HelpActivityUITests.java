package com.yevsp8.checkmanager.view;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.yevsp8.checkmanager.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HelpActivityUITests {

    @Rule
    public ActivityTestRule<HelpActivity> mActivityTestRule = new ActivityTestRule<>(HelpActivity.class);

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void helpActivityUITests() {
        ViewInteraction view = onView(
                allOf(withId(R.id.toolbar_main),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                0),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withId(R.id.help_title), withText(R.string.help_fragment_title),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.help_fragmentcontainer),
                                        0),
                                0),
                        isDisplayed()));
        textView.check(matches(withText(R.string.help_fragment_title)));

        ViewInteraction textView2 = onView(
                allOf(withId(R.id.help_textBeforeListitems), withText(R.string.help_fragment_textBeforelistitems),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.help_fragmentcontainer),
                                        0),
                                1),
                        isDisplayed()));
        textView2.check(matches(withText(R.string.help_fragment_textBeforelistitems)));

        ViewInteraction textView3 = onView(
                allOf(withId(R.id.help_listitem_0), withText(R.string.help_fragment_listitem_0),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.help_fragmentcontainer),
                                        0),
                                2),
                        isDisplayed()));
        textView3.check(matches(withText(R.string.help_fragment_listitem_0)));

        ViewInteraction textView4 = onView(
                allOf(withId(R.id.help_listitem_1), withText(R.string.help_fragment_listitem_1),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.help_fragmentcontainer),
                                        0),
                                3),
                        isDisplayed()));
        textView4.check(matches(withText(R.string.help_fragment_listitem_1)));

        ViewInteraction textView5 = onView(
                allOf(withId(R.id.help_listitem_2), withText(R.string.help_fragment_listitem_2),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.help_fragmentcontainer),
                                        0),
                                4),
                        isDisplayed()));
        textView5.check(matches(withText(R.string.help_fragment_listitem_2)));

        ViewInteraction textView6 = onView(
                allOf(withId(R.id.help_listitem_3), withText(R.string.help_fragment_listitem_3),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.help_fragmentcontainer),
                                        0),
                                5),
                        isDisplayed()));
        textView6.check(matches(withText(R.string.help_fragment_listitem_3)));

        ViewInteraction textView7 = onView(
                allOf(withId(R.id.help_programUsage), withText(R.string.help_fragment_programUsage),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.help_fragmentcontainer),
                                        0),
                                6),
                        isDisplayed()));
        textView7.check(matches(withText(R.string.help_fragment_programUsage)));

    }
}
