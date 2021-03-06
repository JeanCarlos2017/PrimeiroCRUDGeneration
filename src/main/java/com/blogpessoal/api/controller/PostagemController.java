package com.blogpessoal.api.controller;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blogpessoal.domain.model.Postagem;
import com.blogpessoal.domain.service.CadastroPostagemService;

@RestController
@RequestMapping("/postagem")
@CrossOrigin("*")
public class PostagemController {
	
	
	@Autowired
	private CadastroPostagemService cadastroPostagem;
	
	@GetMapping("")
	public ResponseEntity<List<Postagem>> findAll() {
		return ResponseEntity.ok(cadastroPostagem.findAll());
	}
	
	@GetMapping("/{postId}")
	public ResponseEntity<Postagem> findById(@PathVariable Long postId) {
		Optional<Postagem> post = cadastroPostagem.findById(postId);
		if (post.isPresent()) {
			return ResponseEntity.ok(post.get());
		}else {
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/titulo/{titulo}")
	public ResponseEntity<List<Postagem>> findAllByTituloContaining(@PathVariable String titulo) {
		return ResponseEntity.ok(cadastroPostagem.findAllByTituloContainingIgnoreCase(titulo));
	}
	
	@PostMapping
	public ResponseEntity<Postagem> adicionarPostagem(@RequestBody Postagem postagem) {
		return ResponseEntity.status(HttpStatus.CREATED).body(cadastroPostagem.salvar(postagem));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Postagem> alteraPostagem(@Valid @PathVariable Long id, @RequestBody Postagem post) {
		return ResponseEntity.status(HttpStatus.OK).body(this.cadastroPostagem.alteraPostagem(post, id));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deletePostagem(@Valid @PathVariable Long id) {
		this.cadastroPostagem.deletePostagem(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
