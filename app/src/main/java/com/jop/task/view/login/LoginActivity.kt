package com.jop.task.view.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.jop.task.R
import com.jop.task.base.ui.BaseActivity
import com.jop.task.data.model.User
import com.jop.task.data.viewModel.UserViewModel
import com.jop.task.data.viewModel.factory.UserViewModelFactory
import com.jop.task.databinding.ActivityLoginBinding
import com.jop.task.view.home.HomeActivity
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : BaseActivity(), KodeinAware {
    override val kodein by kodein()
    private val binding by lazy { ActivityLoginBinding.inflate(layoutInflater) }
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var gso : GoogleSignInOptions
    private val loginVMF: UserViewModelFactory by instance()
    private lateinit var loginVM: UserViewModel

    companion object {
        @JvmStatic
        fun newInstance(context: Context?): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private val getResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if(it.resultCode == Activity.RESULT_OK){
            val  accountTask : Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try {
                val googleSignInAccount : GoogleSignInAccount = accountTask.getResult(ApiException::class.java)
                val authCredential : AuthCredential = GoogleAuthProvider.getCredential(googleSignInAccount.idToken, null)

                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener {
                    if(it.isSuccessful){
                        loginVM.insertUser(User(
                            name = googleSignInAccount.displayName ?: "",
                            email = googleSignInAccount.email ?: "",
                            token = googleSignInAccount.id ?: ""
                        ))

                        val intent = HomeActivity.newInstance(this)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        showFailedToast("Failed Sign In with google account ${it.exception}")
                    }
                }
            } catch (e: Exception){
                showFailedToast("Failed Sign In with google account ${e.message}")
            }
            hideLoadingDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        loginVM = ViewModelProvider(this, loginVMF)[UserViewModel::class.java]

        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)

        binding.apply {
            btnGoogle.setOnClickListener {
                showLoadingDialog()
                val intent: Intent = mGoogleSignInClient.signInIntent
                getResult.launch(intent)
            }
        }
    }

    override fun onStart() {
        if(firebaseAuth.currentUser != null){
            val intent = HomeActivity.newInstance(this)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            finish()
        }
        super.onStart()
    }
}