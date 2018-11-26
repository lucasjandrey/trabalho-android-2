package br.edu.unidavi.trabalhoandroid.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import br.edu.unidavi.trabalhoandroid.R;
import br.edu.unidavi.trabalhoandroid.dados.LocalDatabaseController;
import br.edu.unidavi.trabalhoandroid.eventbus.Carro;
import br.edu.unidavi.trabalhoandroid.eventbus.Favorito;

public class CarroDetalheActivity extends AppCompatActivity implements OnMapReadyCallback {
    private Context context;
    private TextView marca;
    private TextView modelo;
    private TextView ano;
    private TextView preco;
    private TextView observacoes;
    private Button addFavoritos;
    private ImageView imagem;

    private int idServer;
    private String marcaTxt;
    private String modeloTxt;
    private String anoTxt;
    private String imagemTxt;
    private String precoTxt;

    private ArrayList<Favorito> favoritoArrayList;
    private LocalDatabaseController db;

    private GoogleMap mMap;
    private boolean enableMyLocation = false;

    // O marker começa na Unidavi
    private LatLng previousPosition = new LatLng(-27.2104016, -49.6466032);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carro_detalhe);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        db = new LocalDatabaseController(this);

        Button det_btnVoltar;

        marca = findViewById(R.id.det_txtMarca);
        modelo = findViewById(R.id.det_txtModelo);
        ano = findViewById(R.id.det_txtAno);
        preco = findViewById(R.id.det_txtPreco);
        observacoes = findViewById(R.id.det_txtObservacoesDetalhe);
        imagem = findViewById(R.id.det_imagemIcone);
        addFavoritos = findViewById(R.id.det_btnAddFavoritos);
        det_btnVoltar = findViewById(R.id.det_btnVoltar);

        det_btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //Busca todos os favoritos no SQLite cada vez que a tela é aberta
        favoritoArrayList = db.getAllFavs();

        addFavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.inserirFavorito(idServer, marcaTxt, modeloTxt, anoTxt, imagemTxt, precoTxt);
                addFavoritos.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Adicionado a Lista de Favorito", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();

        EventBus.getDefault().unregister(this);
    }

    @Subscribe (sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent (Carro carro) {
        idServer = carro.getIdServer();
        marcaTxt = carro.getMarca();
        modeloTxt = carro.getModelo();
        anoTxt = carro.getAno();
        imagemTxt = carro.getImagem();
        precoTxt = carro.getPreco();

        marca.setText(carro.getMarca());
        modelo.setText(carro.getModelo());
        ano.setText(carro.getAno());
        preco.setText(carro.getPreco());
        observacoes.setText(carro.getObservacoes());

        Picasso.with(context)
                .load(carro.getImagem())
                .into(imagem);

        //Se um carro já está na lista de favoritos oculta o botão "Add aos Favoritos"
        //Essa função foi colocada aqui para dar tempo desta classe receber os dados que vem da no EventBus
        for (int i = 0; i < favoritoArrayList.size(); i++) {
            if (favoritoArrayList.get(i).getIdServer() == idServer) {
                addFavoritos.setVisibility(View.GONE);
            }
        }
    }
    private List<Marker> markers = new ArrayList<>();

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                this, R.raw.mapstyle
        ));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(enableMyLocation);
        mMap.setMyLocationEnabled(enableMyLocation);

        LatLng unidavi = new LatLng(-27.2104016, -49.6466032);
        MarkerOptions marker = new MarkerOptions()
                .draggable(true)
                .position(unidavi)
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .title("Remover");
        markers.add(mMap.addMarker(marker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                unidavi, 17f));

        mMap.addMarker(new MarkerOptions()
                .draggable(true)
                .icon(BitmapDescriptorFactory
                        .fromResource(R.drawable.ic_buiding))
                .position(unidavi));
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}