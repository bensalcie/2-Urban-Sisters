package apps.bensalcie.a2urbansisters.logincontroller

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import apps.bensalcie.a2urbansisters.productscontroller.HomeActivity
import apps.bensalcie.a2urbansisters.R
import apps.bensalcie.a2urbansisters.admin.AdminActivity
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private var root: View?=null
    var ctx: Context?=null
    private var  tvForgotPassword:TextView?=null

    var auth :FirebaseAuth?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        ctx= container!!.context
        root=  inflater.inflate(R.layout.fragment_login, container, false)
        val etUsername = root!!.findViewById<EditText>(R.id.etUserName)
        val etPassword = root!!.findViewById<EditText>(R.id.etpassword)
        val btnLogin =root!!.findViewById<Button>(R.id.btn_login)
        val progressBar =root!!.findViewById<ProgressBar>(R.id.loginProgress)
        val tvCreateAccount =root!!.findViewById<TextView>(R.id.tvNavRegister)
        tvForgotPassword=root!!.findViewById(R.id.tvForgotPassword)

        auth= FirebaseAuth.getInstance()

        tvCreateAccount.setOnClickListener {
            findNavController().navigate(R.id.registerFragment)
        }
        btnLogin.setOnClickListener {


            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            if (username.isNotBlank() && password.isNotBlank())
            {
                progressBar.visibility = View.VISIBLE
                btnLogin.text= getString(R.string.loading)
                loginUser(username,password,progressBar,btnLogin)
            }else{
                etUsername.error = "Check"
                etPassword.error= "Check"
            }
        }
        return root
    }

    //sister@gmail.com,123456-->test email
    //admin.sister@gmail.com,123456

    private fun loginUser(
        username: String,
        password: String,
        progressBar: ProgressBar?,
        btnLogin: Button?
    ) {
        auth?.signInWithEmailAndPassword(username, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    progressBar!!.visibility=View.GONE
                    btnLogin!!.text=getString(R.string.success)
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Login", "createUserWithEmail:success")
//                    val user = auth!!.currentUser
                    val user = auth?.currentUser

                    if (user != null) {
                        if (user.email=="admin.sister@gmail.com") {
                            sendToAdmin()
                        }else{
                            sendToHome()

                        }
                    }

                } else {
                    progressBar!!.visibility=View.GONE
                    btnLogin!!.text=getString(R.string.tryAgain)
                    // If sign in fails, display a message to the user.
                    Log.w("Login", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(ctx, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }


    }


    }

    override fun onStart() {
        super.onStart()
        val user = auth?.currentUser
        if (user!=null){
            if (user.email=="admin.sister@gmail.com")
            {
                sendToAdmin()
            }else{
                sendToHome()

            }
        }

    }

    private fun sendToHome() {
        val intent = Intent(ctx,
            HomeActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
    private fun sendToAdmin() {
        val intent = Intent(ctx,AdminActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }

}