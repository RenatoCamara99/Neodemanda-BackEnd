package com.neoenergia.neodemanda.domain.model;

import com.neoenergia.neodemanda.domain.enums.StatusProjeto;
import com.neoenergia.neodemanda.domain.enums.TensaoAtendimento;
import com.neoenergia.neodemanda.domain.enums.TipoEdificacao;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Projeto eletrico de uma edificacao com multiplas unidades consumidoras.
 *
 * <p>E a raiz do dominio do NeoDemanda: reune os dados cadastrais do projeto
 * protocolado na distribuidora (identificacao, responsavel tecnico, localizacao)
 * e as grandezas eletricas que alimentam o calculo normativo de demanda - carga
 * instalada declarada e demanda resultante, alem do nivel de tensao de
 * atendimento.
 *
 * <p>O ciclo de vida do projeto e acompanhado por {@link StatusProjeto}, de
 * {@code RASCUNHO} ate a aprovacao ou reprovacao pela analise da distribuidora.
 *
 * <p>Todos os valores eletricos usam {@link BigDecimal}: o calculo normativo e
 * confrontado com memoriais e pareceres, portanto nao tolera o erro de
 * arredondamento dos tipos de ponto flutuante binario.
 */
@Entity
@Table(name = "projeto")
public class Projeto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/** Protocolo do projeto no SGPEO; identifica o processo junto a distribuidora. */
	@NotBlank
	@Size(max = 50)
	@Column(name = "protocolo_sgpeo", nullable = false, unique = true, length = 50)
	private String protocoloSgpeo;

	@NotBlank
	@Size(max = 150)
	@Column(name = "nome_projeto", nullable = false, length = 150)
	private String nomeProjeto;

	@NotBlank
	@Size(max = 150)
	@Column(name = "construtora", nullable = false, length = 150)
	private String construtora;

	@NotBlank
	@Size(max = 150)
	@Column(name = "responsavel_tecnico", nullable = false, length = 150)
	private String responsavelTecnico;

	/** Registro no CREA do responsavel tecnico pelo projeto. */
	@NotBlank
	@Size(max = 30)
	@Column(name = "crea_responsavel", nullable = false, length = 30)
	private String creaResponsavel;

	@NotBlank
	@Size(max = 255)
	@Column(name = "endereco", nullable = false, length = 255)
	private String endereco;

	@NotBlank
	@Size(max = 100)
	@Column(name = "municipio", nullable = false, length = 100)
	private String municipio;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_edificacao", nullable = false, length = 20)
	private TipoEdificacao tipoEdificacao;

	/** Numero de unidades consumidoras atendidas pela entrada coletiva. */
	@NotNull
	@Min(1)
	@Column(name = "quantidade_unidades_consumidoras", nullable = false)
	private Integer quantidadeUnidadesConsumidoras;

	/** Carga instalada declarada, em kVA. */
	@NotNull
	@Positive
	@Column(name = "carga_instalada_kva", nullable = false, precision = 12, scale = 3)
	private BigDecimal cargaInstaladaKva;

	/**
	 * Demanda resultante do calculo normativo, em kVA.
	 *
	 * <p>Permanece nula ate que o calculo seja executado sobre o projeto.
	 */
	@PositiveOrZero
	@Column(name = "demanda_calculada_kva", precision = 12, scale = 3)
	private BigDecimal demandaCalculadaKva;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "tensao_atendimento", nullable = false, length = 20)
	private TensaoAtendimento tensaoAtendimento;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 20)
	private StatusProjeto status;

	@Column(name = "data_criacao", nullable = false, updatable = false)
	private LocalDateTime dataCriacao;

	@Column(name = "data_atualizacao", nullable = false)
	private LocalDateTime dataAtualizacao;

	/** Construtor sem argumentos exigido pela JPA. */
	public Projeto() {
	}

	/**
	 * Cria um projeto com os dados informados pelo solicitante.
	 *
	 * <p>Nao recebe {@code id}, {@code dataCriacao} nem {@code dataAtualizacao}:
	 * o primeiro e gerado pelo banco e os demais sao preenchidos pelos callbacks
	 * {@link #aoPersistir()} e {@link #aoAtualizar()}.
	 */
	public Projeto(String protocoloSgpeo, String nomeProjeto, String construtora, String responsavelTecnico,
			String creaResponsavel, String endereco, String municipio, TipoEdificacao tipoEdificacao,
			Integer quantidadeUnidadesConsumidoras, BigDecimal cargaInstaladaKva, BigDecimal demandaCalculadaKva,
			TensaoAtendimento tensaoAtendimento, StatusProjeto status) {
		this.protocoloSgpeo = protocoloSgpeo;
		this.nomeProjeto = nomeProjeto;
		this.construtora = construtora;
		this.responsavelTecnico = responsavelTecnico;
		this.creaResponsavel = creaResponsavel;
		this.endereco = endereco;
		this.municipio = municipio;
		this.tipoEdificacao = tipoEdificacao;
		this.quantidadeUnidadesConsumidoras = quantidadeUnidadesConsumidoras;
		this.cargaInstaladaKva = cargaInstaladaKva;
		this.demandaCalculadaKva = demandaCalculadaKva;
		this.tensaoAtendimento = tensaoAtendimento;
		this.status = status;
	}

	/** Carimba a criacao e assume RASCUNHO quando o status nao foi informado. */
	@PrePersist
	protected void aoPersistir() {
		LocalDateTime agora = LocalDateTime.now();
		this.dataCriacao = agora;
		this.dataAtualizacao = agora;
		if (this.status == null) {
			this.status = StatusProjeto.RASCUNHO;
		}
	}

	@PreUpdate
	protected void aoAtualizar() {
		this.dataAtualizacao = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProtocoloSgpeo() {
		return protocoloSgpeo;
	}

	public void setProtocoloSgpeo(String protocoloSgpeo) {
		this.protocoloSgpeo = protocoloSgpeo;
	}

	public String getNomeProjeto() {
		return nomeProjeto;
	}

	public void setNomeProjeto(String nomeProjeto) {
		this.nomeProjeto = nomeProjeto;
	}

	public String getConstrutora() {
		return construtora;
	}

	public void setConstrutora(String construtora) {
		this.construtora = construtora;
	}

	public String getResponsavelTecnico() {
		return responsavelTecnico;
	}

	public void setResponsavelTecnico(String responsavelTecnico) {
		this.responsavelTecnico = responsavelTecnico;
	}

	public String getCreaResponsavel() {
		return creaResponsavel;
	}

	public void setCreaResponsavel(String creaResponsavel) {
		this.creaResponsavel = creaResponsavel;
	}

	public String getEndereco() {
		return endereco;
	}

	public void setEndereco(String endereco) {
		this.endereco = endereco;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public TipoEdificacao getTipoEdificacao() {
		return tipoEdificacao;
	}

	public void setTipoEdificacao(TipoEdificacao tipoEdificacao) {
		this.tipoEdificacao = tipoEdificacao;
	}

	public Integer getQuantidadeUnidadesConsumidoras() {
		return quantidadeUnidadesConsumidoras;
	}

	public void setQuantidadeUnidadesConsumidoras(Integer quantidadeUnidadesConsumidoras) {
		this.quantidadeUnidadesConsumidoras = quantidadeUnidadesConsumidoras;
	}

	public BigDecimal getCargaInstaladaKva() {
		return cargaInstaladaKva;
	}

	public void setCargaInstaladaKva(BigDecimal cargaInstaladaKva) {
		this.cargaInstaladaKva = cargaInstaladaKva;
	}

	public BigDecimal getDemandaCalculadaKva() {
		return demandaCalculadaKva;
	}

	public void setDemandaCalculadaKva(BigDecimal demandaCalculadaKva) {
		this.demandaCalculadaKva = demandaCalculadaKva;
	}

	public TensaoAtendimento getTensaoAtendimento() {
		return tensaoAtendimento;
	}

	public void setTensaoAtendimento(TensaoAtendimento tensaoAtendimento) {
		this.tensaoAtendimento = tensaoAtendimento;
	}

	public StatusProjeto getStatus() {
		return status;
	}

	public void setStatus(StatusProjeto status) {
		this.status = status;
	}

	public LocalDateTime getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(LocalDateTime dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public LocalDateTime getDataAtualizacao() {
		return dataAtualizacao;
	}

	public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
		this.dataAtualizacao = dataAtualizacao;
	}

	/**
	 * Igualdade por identidade persistente: duas instancias so sao iguais quando
	 * representam a mesma linha. Projetos ainda nao persistidos ({@code id} nulo)
	 * sao iguais apenas a si mesmos.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Projeto outro)) {
			return false;
		}
		return this.id != null && this.id.equals(outro.id);
	}

	/**
	 * Hash constante de proposito: o {@code id} so existe apos o flush, e um hash
	 * derivado dele mudaria com a entidade ja dentro de um {@code HashSet}.
	 */
	@Override
	public int hashCode() {
		return Projeto.class.hashCode();
	}

	/** Omite responsavel tecnico, CREA e endereco por serem dados pessoais. */
	@Override
	public String toString() {
		return "Projeto{id=" + id
				+ ", protocoloSgpeo=" + protocoloSgpeo
				+ ", nomeProjeto=" + nomeProjeto
				+ ", construtora=" + construtora
				+ ", municipio=" + municipio
				+ ", tipoEdificacao=" + tipoEdificacao
				+ ", quantidadeUnidadesConsumidoras=" + quantidadeUnidadesConsumidoras
				+ ", cargaInstaladaKva=" + cargaInstaladaKva
				+ ", demandaCalculadaKva=" + demandaCalculadaKva
				+ ", tensaoAtendimento=" + tensaoAtendimento
				+ ", status=" + status
				+ ", dataCriacao=" + dataCriacao
				+ ", dataAtualizacao=" + dataAtualizacao
				+ "}";
	}

}
