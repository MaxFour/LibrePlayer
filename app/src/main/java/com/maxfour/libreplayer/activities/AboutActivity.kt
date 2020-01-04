package com.maxfour.libreplayer.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.core.app.ShareCompat
import com.maxfour.appthemehelper.util.ATHUtil
import com.maxfour.appthemehelper.util.ToolbarContentTintHelper
import com.maxfour.libreplayer.Constants.GITHUB_PROJECT
import com.maxfour.libreplayer.R
import com.maxfour.libreplayer.activities.base.AbsBaseActivity
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.card_player_info.*
import kotlinx.android.synthetic.main.card_version.*

class AboutActivity : AbsBaseActivity(), View.OnClickListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		setDrawUnderStatusBar()
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_about)
		setStatusbarColorAuto()
		setNavigationbarColorAuto()
		setLightNavigationBar(true)

		val toolbarColor = ATHUtil.resolveColor(this, R.attr.colorSurface)
		toolbar.setBackgroundColor(toolbarColor)
		ToolbarContentTintHelper.colorBackButton(toolbar)
		setSupportActionBar(toolbar)
		version.setSummary(getAppVersion())
		setUpView()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			onBackPressed()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	private fun openUrl(url: String) {
		val i = Intent(Intent.ACTION_VIEW)
		i.data = Uri.parse(url)
		i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
		startActivity(i)
	}

	private fun setUpView() {
		appGithub.setOnClickListener(this)
		appShare.setOnClickListener(this)
	}

	override fun onClick(view: View) {
		when (view.id) {
			R.id.appGithub -> openUrl(GITHUB_PROJECT)
			R.id.appShare -> shareApp()
		}
	}

	private fun getAppVersion(): String {
		return try {
			val packageInfo = packageManager.getPackageInfo(packageName, 0)
			packageInfo.versionName
		} catch (e: PackageManager.NameNotFoundException) {
			e.printStackTrace()
			"0.0.0"
		}
	}

	private fun shareApp() {
		ShareCompat.IntentBuilder.from(this).setType("text/plain")
			.setChooserTitle(R.string.share_app)
			.setText(String.format(getString(R.string.app_share), packageName)).startChooser()
	}
}
