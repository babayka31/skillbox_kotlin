package com.skillbox.github.ui.detail_repositories

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.skillbox.github.App
import com.skillbox.github.R
import com.skillbox.github.models.Repositories
import com.skillbox.github.network.Networking
import com.skillbox.github.utils.toEditable
import com.skillbox.github.utils.toast
import kotlinx.android.synthetic.main.dialog_repo_details.*
import kotlinx.android.synthetic.main.dialog_repo_details.ownerAvatarIV
import kotlinx.android.synthetic.main.item_repo.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailRepoFragment : Fragment(R.layout.dialog_repo_details) {

    private val args: DetailRepoFragmentArgs by navArgs()
    private var checkStarred = false

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setParam(args.idRepo)

    }

    private fun setParam(position: Int) {
        val item = App.repositories?.repositoriesAdapter?.items?.get(position)!!
        idETt.text = item.id.toString().toEditable()
        repoNameETt.text = item.header.toEditable()
        repoFullNameETt.text = item.fullName.toEditable()
        repoDescETt.text = item.description?.toEditable() ?: "empty".toEditable()
        repoHtmlUrlETt.text = item.html_url?.toEditable() ?: "empty".toEditable()
        repoSshUrlETt.text = item.ssh_url?.toEditable() ?: "empty".toEditable()
        Glide.with(requireView())
            .load(item.owner.avatar_url)
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_baseline_photo_size_select_actual_24)
            .error(R.drawable.ic_baseline_image_not_supported_24)
            .into(ownerAvatarIV)

        App.repositories!!.viewModel.getCheckStarred(
            item,
            onComplete = { check ->
                if(check){
                    checkStarred = true
                    starredRepoIV.setBackgroundColor(Color.YELLOW)
                } else {
                    checkStarred = false
                    starredRepoIV.setBackgroundColor(Color.GRAY)
                }
            },
            onError = { mes ->
                toast("Ошибка: $mes")
            })

        starredRepoIV.setOnClickListener {
            if(checkStarred){
                deleteStarred(item)
            } else {
                putStarred(item)
            }
        }
    }

    private fun putStarred(item:Repositories){
        App.repositories!!.viewModel.putStarred(
            item,
            onComplete = { check ->
                if(check){
                    toast("Звездочка успешно поставлена")
                    starredRepoIV.setBackgroundColor(Color.YELLOW)
                    checkStarred = true
                }
            },
            onError = { mes ->
                toast("Ошибка: $mes")
            }
        )
    }

    private fun deleteStarred(item:Repositories){
        App.repositories!!.viewModel.deleteStarred(
            item,
            onComplete = { check ->
                if(check) {
                    toast("Звездочка успешно удалена")
                    starredRepoIV.setBackgroundColor(Color.GRAY)
                    checkStarred = false
                }
            },
            onError = { mes ->
                toast("Ошибка: $mes")
            }
        )
    }
}