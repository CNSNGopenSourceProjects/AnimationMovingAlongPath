package br.com.conseng.animationmovingalongpath

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import br.com.conseng.animationmovingalongpath.R.id.animationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val animationViewArea = findViewById<R.id.an>()

        val avatars = hashSetOf(R.drawable.eric_cartman, R.drawable.kenny_mc_cormick,
                R.drawable.kyle_broflovski, R.drawable.timmy_burch,
                R.drawable.stan_marsh, R.drawable.wendy_testaburger,
                R.drawable.token_black, R.drawable.clyde_donovan)
        val players = ArrayList<PlayerDto>()
        for (i in avatars.indices) {
            val distance: Float = Constants.PLAYER_DISTANT * i + Constants.PLAYER_SIZE
            val offset: Float = Constants.PLAYER_SIZE / 2
            val playerDto = PlayerDto(resources, avatars.elementAt(i), distance, offset, offset)
            players.add(playerDto)
        }
        animationView..setPlayers(players)
    }
}
