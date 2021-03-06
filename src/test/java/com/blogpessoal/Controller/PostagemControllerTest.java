package com.blogpessoal.Controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;

import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.blogpessoal.api.controller.PostagemController;
import com.blogpessoal.domain.exception.EntidadeNaoEncontradaException;
import com.blogpessoal.domain.model.Postagem;
import com.blogpessoal.domain.repository.PostagemRepository;
import com.blogpessoal.domain.service.CadastroPostagemService;
import com.blogpessoal.util.PostagemCreator;
import com.blogpessoal.util.ResponseEntityPostagemTest;

@ExtendWith(SpringExtension.class)
@DisplayName("[Teste]Postagem Controller")
public class PostagemControllerTest {
	@InjectMocks //quero testar a classe em si
	private PostagemController postagemController;
	@Mock 
	private CadastroPostagemService postService;
	@Mock 
	private PostagemRepository postRepository;
	
	@Test
	@DisplayName("Retorna lista de postagens dentro po postList")
	void testFindAll_Successful() {
		//criando uma lista de postagem com um elemento
		List<Postagem> postList= List.of(PostagemCreator.criaPostagem());
		
		//quando chamar o medodo findAll vai retornar o postagemPage
		BDDMockito.when(postService.findAll())
							.thenReturn(postList);
		ResponseEntity<List<Postagem>> respostaFindAll= postagemController.findAll();
		//realizando os testes 
		ResponseEntityPostagemTest.testeListaPostagem(respostaFindAll, HttpStatus.OK);
		
	}
	
	@Test
	@DisplayName("busca por id Sucesso")
	void findById_Successful() {
		BDDMockito.when(postService.findById(ArgumentMatchers.anyLong()))
				.thenReturn(Optional.of(PostagemCreator.criaPostagem_Save()));
		
		ResponseEntity<Postagem> postFindById = postagemController.findById(321L);
		ResponseEntityPostagemTest.testePostagemUnica(postFindById, HttpStatus.OK);
	}
	
	@Test
	@DisplayName("Busca por titulo: Sucesso")
	void findByTitulo() {
		// criando uma lista de postagem com um elemento
		List<Postagem> postList = List.of(PostagemCreator.criaPostagem());
		BDDMockito.when(
				postService.findAllByTituloContainingIgnoreCase(ArgumentMatchers.anyString()))
				.thenReturn(postList);
		//fazendo a requisição no controller 
		ResponseEntity<List<Postagem>> testFindByTitulo= postagemController.findAllByTituloContaining("titulo");
		// realizando os testes
		ResponseEntityPostagemTest.testeListaPostagem(testFindByTitulo, HttpStatus.OK);
	}
	@Test
	@DisplayName("Busca por Id Inexistente")
	void findByIdInexistente() {
		BDDMockito.when(postService.findById(ArgumentMatchers.anyLong()))
		.thenReturn(Optional.empty());
		//fazendo a requisição no controller 
		ResponseEntity<Postagem> postFindById= postagemController.findById(3021L);
		// realizando os testes
		ResponseEntityPostagemTest.testePostagemUnicaVazia(postFindById, HttpStatus.NOT_FOUND);
	}
	
	@Test 
	@DisplayName("Save Post")
	void savePost_Success() {
		BDDMockito.when(postService.salvar(ArgumentMatchers.any())).thenReturn(PostagemCreator.criaPostagem_Save());
		//fazendo a requisição no controller 
		ResponseEntity<Postagem> postSave = postagemController.adicionarPostagem(PostagemCreator.criaPostagem());
		//realizando os testes 
		ResponseEntityPostagemTest.testePostagemUnica(postSave, HttpStatus.CREATED);
	}
	
	@Test 
	@DisplayName("Delete Post IdExistente")
	void deletePost_Success() {
		//fazendo a requisição no controller 
		ResponseEntity<Void> postDelete= postagemController
								.deletePostagem(PostagemCreator.criaPostagem_Save().getId());
		//realizando os testes 
		Assertions.assertThat(postDelete.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
	}
	
	@Test 
	@DisplayName("Delete Post Id Inexistente")
	void deletePost_failure() {
		doThrow(new EntidadeNaoEncontradaException("entidade não encontrada")).
					when(postService).deletePostagem(ArgumentMatchers.anyLong());
								
		//realizando os testes 
		assertThrows(
				EntidadeNaoEncontradaException.class, 
				()->{
					postagemController.deletePostagem(2567L);
		});
	}
	
	@Test
	@DisplayName("Update Post Id Existente")
	void putPost_Success() {
		BDDMockito.when(postService.alteraPostagem(ArgumentMatchers.any(), ArgumentMatchers.anyLong()))
													.thenReturn(PostagemCreator.criaPostagem_Update());

		// fazendo a requisição no controller
		ResponseEntity<Postagem> postUpdate = postagemController
				.alteraPostagem(PostagemCreator.criaPostagem_Save().getId(), PostagemCreator.criaPostagem_Update());
		ResponseEntityPostagemTest.testePostagemUnica(postUpdate, HttpStatus.OK);
	}
	
	@Test
	@DisplayName("Update Post Id Inexistente")
	void putPost_Failure() {
		BDDMockito.when(postService.alteraPostagem(ArgumentMatchers.any(), ArgumentMatchers.anyLong()))
		.thenThrow(new EntidadeNaoEncontradaException
				("Não existe uma entidade com o id passado! Por favor verifique a requisição e tente novamente"));

		// fazendo a requisição no controller
		assertThrows(
				EntidadeNaoEncontradaException.class, 
				()->{
					postagemController.alteraPostagem(2567L,PostagemCreator.criaPostagem_Update());
		});
		
	}
}
