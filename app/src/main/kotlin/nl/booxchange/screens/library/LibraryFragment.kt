package nl.booxchange.screens.library

import android.graphics.Rect
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_library.*
import kotlinx.android.synthetic.main.fragment_library.view.*
import nl.booxchange.R
import nl.booxchange.extension.setVisible
import nl.booxchange.extension.string
import nl.booxchange.utilities.BaseFragment
import org.jetbrains.anko.dip

class LibraryFragment : BaseFragment() {

    override val contentViewResourceId = R.layout.fragment_library
    override val viewModel = LibraryFragmentViewModel()

    private val userUid = FirebaseAuth.getInstance().currentUser?.uid!!.string
    private val dbRef = FirebaseDatabase.getInstance().reference.child("users/").child(userUid)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(databaseError: DatabaseError) {
                databaseError.toException().printStackTrace()
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val firstName = dataSnapshot.child("first_name").value.toString()
                val lastName = dataSnapshot.child("last_name").value.toString()
                val userPhoto = dataSnapshot.child("imageUrl").value.toString()
                val numberBooks = dataSnapshot.child("books").value
                Glide.with(activity!!.applicationContext).load(userPhoto).apply(RequestOptions().circleCrop()).into(profile_image)
                userName.text = ("$firstName $lastName")
                books.text = numberBooks.toString()
            }
        })

        view.user_books_list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            val `8dp` = view.dip(8)
            val `0dp` = view.dip(0)

            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(`8dp`, `0dp`, `8dp`, `8dp`)
            }
        })
    }
}