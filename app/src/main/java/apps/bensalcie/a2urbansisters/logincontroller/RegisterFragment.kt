package apps.bensalcie.a2urbansisters.logincontroller

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.navigation.fragment.findNavController
import apps.bensalcie.a2urbansisters.R
import com.google.firebase.auth.FirebaseAuth

class RegisterFragment : Fragment() {

    private var root: View?=null
    var ctx: Context?=null
    private var  tvForgotPassword: TextView?=null

    var auth : FirebaseAuth?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ctx= container!!.context
        root=  inflater.inflate(R.layout.fragment_register, container, false)
        val etUsername = root!!.findViewById<EditText>(R.id.etUserName)
        val etPassword = root!!.findViewById<EditText>(R.id.etpassword)
        val etConfirmPassword = root!!.findViewById<EditText>(R.id.etConfirmpassword)

        val btnLogin =root!!.findViewById<Button>(R.id.btn_login)
        val progressBar =root!!.findViewById<ProgressBar>(R.id.loginProgress)

        auth= FirebaseAuth.getInstance()
        btnLogin.setOnClickListener {


            val username = etUsername.text.toString()
            val password = etPassword.text.toString()
            val cpassword = etConfirmPassword.text.toString()
            if (username.isNotBlank() && password.isNotBlank() && cpassword.isNotBlank())
            {
                if (cpassword == password)
                {
                    progressBar.visibility = View.VISIBLE
                    btnLogin.text= getString(R.string.loading)
                    createUser(username,password,progressBar,btnLogin)
                }else{
                    Toast.makeText(ctx, "Passwords dont match...", Toast.LENGTH_SHORT).show()
                }
            }else{
                etUsername.error = "Check"
                etPassword.error= "Check"
            }
        }
        return root
    }

    private fun createUser(username: String, password: String, progressBar: ProgressBar?, btnLogin: Button?) {
        auth?.createUserWithEmailAndPassword(username, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    btnLogin!!.text=getString(R.string.success)
                    progressBar!!.visibility=View.GONE
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Register", "createUserWithEmail:success")
//                    val user = auth!!.currentUser

                    auth!!.signOut()
                    findNavController().navigate(R.id.loginFragment)
                    Toast.makeText(ctx, "Verify your details...", Toast.LENGTH_SHORT).show()
                } else {
                    btnLogin!!.text=getString(R.string.tryAgain)
                    progressBar!!.visibility=View.GONE
                    // If sign in fails, display a message to the user.
                    Log.w("Register", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(ctx, "Registration failed.",
                        Toast.LENGTH_SHORT).show()
                }


            }

    }


}