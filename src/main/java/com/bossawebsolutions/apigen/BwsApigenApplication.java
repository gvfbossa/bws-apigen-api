package com.bossawebsolutions.apigen;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.bossawebsolutions.apigen"})
public class BwsApigenApplication {

	public static void main(String[] args) {

		String env = System.getenv("ENV");

		if (!"docker".equals(env)) {
			String profile = System.getenv("SPRING_PROFILES_ACTIVE");
			if (profile == null) profile = "dev";

			String filename = profile.equals("prod") ? ".env" : ".env.dev";

			Dotenv dotenv = Dotenv.configure()
					.ignoreIfMissing()
					.filename(filename)
					.load();

			dotenv.entries().forEach(entry ->
					System.setProperty(entry.getKey(), entry.getValue())
			);
		}
		SpringApplication.run(BwsApigenApplication.class, args);
	}

}
/**
 * V2
 *
 * 🔎 1. Smart Query Generation
 * Gerar automaticamente endpoints de busca baseados nos campos da entidade.
 *
 * 📄 2. Paginação automática
 * Gerar endpoints já suportando:
 * ?page=0
 * &size=20
 * &sort=name
 *
 * 🔗 3. Suporte a relacionamentos JPA
 * Detectar automaticamente:
 * @OneToMany
 * @ManyToOne
 * @OneToOne
 * @ManyToMany
 *
 * 🧪 4. Geração automática de testes
 * Gerar:
 * ServiceTest
 * ControllerTest
 * RepositoryTest
 *
 * 📜 5. Swagger / OpenAPI automático
 * Gerar anotações:
 * @Operation
 * @ApiResponse
 * @Tag
 *
 * ✔️ 6. Validações automáticas no DTO
 * Detectar anotações da entity:
 * @NotNull
 * @Size
 * @Email
 * @Min
 * @Max
 *
 * 🧠 7. Detecção de Lombok
 * O generator já detecta parcialmente, mas pode evoluir para:
 * @Data
 * @Builder
 * @NoArgsConstructor
 * @AllArgsConstructor
 *
 * 🧩 8. Plugin system para generators
 * Permitir adicionar novos geradores sem mexer no core:
 * apigen-generator-swagger
 * apigen-generator-tests
 * apigen-generator-validation
 *
 * 🌐 9. Suporte a múltiplos frameworks
 * Spring Boot
 * NestJS
 * FastAPI
 * Django
 *
 * ⚙️ 10. Configuração via arquivo
 * Adicionar suporte a um arquivo no projeto:
 * apigen.yml
 *
 * 📊 11. Dashboard no SaaS
 * Usuário ver:
 * histórico de gerações
 * projetos
 * estatísticas
 *
 * 💳 12. Gestão de licenças da CLI
 * No SaaS:
 * tokens ativos
 * máquinas autorizadas
 * revogar acesso
 *
 * 📁 13. Geração incremental
 * gera apenas arquivos que não existem
 *
 * 🧰 14. Templates customizáveis
 * Usuário poderia sobrescrever templates:
 * .apigen/templates/controller.ftl
 *
 */