package uk.co.q3c.v7.base.view;

import javax.inject.Inject;

import uk.co.q3c.v7.base.guice.uiscope.UIScoped;
import uk.co.q3c.v7.base.navigate.V7Navigator;

@UIScoped
public class DefaultRequestSystemAccountView extends StandardPageViewBase implements RequestSystemAccountView {

	@Inject
	protected DefaultRequestSystemAccountView(V7Navigator navigator) {
		super(navigator);
	}

}
