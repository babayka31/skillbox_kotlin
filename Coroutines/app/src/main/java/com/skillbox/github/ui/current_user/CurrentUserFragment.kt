package com.skillbox.github.ui.current_user

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.skillbox.github.R
import com.skillbox.github.ui.adapter.FollowingAdapter
import com.skillbox.github.ui.adapter.RepositoriesAdapter
import com.skillbox.github.utils.autoCleared
import com.skillbox.github.utils.toEditable
import com.skillbox.github.utils.toast
import kotlinx.android.synthetic.main.fragment_current_user.*
import kotlinx.android.synthetic.main.fragment_repos_list.*

class CurrentUserFragment : Fragment(R.layout.fragment_current_user) {

    private val viewModel: CurrentUserViewModel by viewModels()
    var followingAdapter: FollowingAdapter by autoCleared()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initList()
        bindViewModel()
    }

    override fun onStart() {
        super.onStart()
        //загружаем информацию профиля пользователя
        Handler().post {
            viewModel.getUserProfile()
        }
    }

    private fun initList() {
        followingAdapter = FollowingAdapter()
        followingList.apply {
            adapter = followingAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun bindViewModel() {
        editButton.setOnClickListener { viewModel.editForm() }
        viewModel.followingLiveData.observe(viewLifecycleOwner) { followingAdapter.items = it }
        viewModel.editProfileLiveData.observe(viewLifecycleOwner, ::updateIsEditProfile)
        viewModel.profileLiveData.observe(viewLifecycleOwner) {
            loginETt.text = it.login.toEditable()
            idETt.text = it.id.toString().toEditable()
            publicReposETt.text = it.public_repos.toString().toEditable()
            nameETt.text = it.name.toEditable()
            emailETt.text = it.email?.toEditable()
            companyETt.text = it.company.toEditable()
            locationETt.text = it.city.toEditable()
            Glide.with(requireContext())
                .load(it.avatar_url)
                .transform(CircleCrop())
                .placeholder(R.drawable.ic_baseline_photo_size_select_actual_24)
                .error(R.drawable.ic_baseline_image_not_supported_24)
                .into(avatarTV)
        }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            toast(it)
        }
    }

    private fun updateIsEditProfile(isEditProfile: Boolean) {
        with(isEditProfile){
            loginET.isEnabled = this
            idET.isEnabled = this
            publicReposET.isEnabled = this
            nameET.isEnabled = this
            emailET.isEnabled = this
            companyET.isEnabled = this
            locationET.isEnabled = this
            hireable_dropdown.isEnabled = this
        }
    }
}