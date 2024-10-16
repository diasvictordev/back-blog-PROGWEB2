package com.progweb2.blog.service;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.progweb2.blog.model.Post;
import com.progweb2.blog.model.Usuario;
import com.progweb2.blog.repository.PostRepository;
import com.progweb2.blog.repository.UsuarioRepository;
import com.progweb2.blog.service.exceptions.RegraDeNegocioException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.Time;
import java.time.*;
import java.util.Base64;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PostRepository postRepository;

    public Post postar(Long id, Post post){
        Usuario usuario =  usuarioRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o usuário!")
        );
        ZonedDateTime now = ZonedDateTime.now(ZoneId.of("America/Sao_Paulo"));
        LocalDate hoje = now.toLocalDate();
        LocalDateTime tempoAtual = now.toLocalDateTime();
        Post postagem = new Post();
        postagem.setTitulo(post.getTitulo());
        postagem.setPost(post.getPost());
        postagem.setData(hoje);
        postagem.setTempo(tempoAtual);
        postagem.setUsuario(usuario);
        byte [] audio = gerarAudio(post.getPost());
        postagem.setAudio(audio);
        return postRepository.save(postagem);
    }

    public Post getPostById(Long id){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o post!")
        );
        return post;
    }

    public List<Post> getAllPosts(){
        List<Post> posts = postRepository.findAll();
        if (!posts.isEmpty()) {
            for (Post post : posts) {
                if (post.getAudio() != null) {
                    // Supondo que o áudio esteja armazenado como um byte array
                    byte[] audioData = post.getAudio();

                    // Você pode converter o áudio para um Base64 string, por exemplo
                    String audioBase64 = Base64.getEncoder().encodeToString(audioData);

                    // Define a URL de áudio para ser utilizada no front-end
                    post.setAudioUrl("data:audio/wav;base64," + audioBase64);
                }
            }
        }

        return posts;
    }

    public List<Post> getPostByUsuario(Long id){
        Usuario usuario =  usuarioRepository.findById(id).orElseThrow(
                () -> new RegraDeNegocioException("Não foi possível encontrar o usuário!")
        );
        return postRepository.findByUsuarioId(id);
    }

    public void deletePost(Long id){
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post não encontrado!"));
        postRepository.delete(post);
    }

    private static final String API_URL = "https://api.elevenlabs.io/v1/text-to-speech/nPczCjzI2devNBz1zQrb/stream";
    private static final String XI_API_KEY = "8845a1d33977e23cec2b164b1f5f03e4";

    public byte[] gerarAudio(String texto) {
        try {
            String body =  String.format("{\n" +
                    "  \"text\": \"%s\",\n" +
                    "  \"model_id\": \"eleven_multilingual_v2\",\n" +
                    "  \"voice_settings\": {\n" +
                    " \"stability\": 0.9,\n" +
                    "    \"similarity_boost\": 0.8,\n" +
                    "    \"use_speaker_boost\": true\n" +
                    "  },\n" +
                    "  \"pronunciation_dictionary_locators\": [\n" +
                    "    {\n" +
                    "      \"pronunciation_dictionary_id\": \"A37HOz8UJ8L31t2DLqes\",\n" +
                    "      \"version_id\": \"pU9Ryd0zIQiBIkoio3XB\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"pronunciation_dictionary_id\": \"rmcLNMbw7VGCmHan1pgk\",\n" +
                    "      \"version_id\": \"s4PE0iJY8B2iN1G3bLxu\"\n" +
                    "    },\n" +
                    "    {\n" +
                    "      \"pronunciation_dictionary_id\": \"yjECZ9QwAfGeBGbqSLwC\",\n" +
                    "      \"version_id\": \"WMwKKT5EKjXhTPjWvTGF\"\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}", texto);
            //body = body.replace("?", textoGerado);

            HttpResponse<InputStream> response = Unirest.post(API_URL)
                    .header("Content-Type", "application/json")
                    .header("xi-api-key", XI_API_KEY)
                    .body(body.getBytes())
                    .asBinary();

            if (response.getStatus() == HttpStatus.OK.value()) {
                return inputStreamToByteArray(response.getBody());
            } else {
                System.err.println("Erro na requisição: " + response.getStatus());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private byte[] inputStreamToByteArray(InputStream inputStream) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                baos.write(buffer, 0, length);
            }
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
