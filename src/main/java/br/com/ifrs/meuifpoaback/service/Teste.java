package br.com.ifrs.meuifpoaback.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.util.Base64;

public class Teste {
    public static void main(String[] args) {


        String html = "<html class=\"background\" xmlns=\"http://www.w3.org/1999/xhtml\"><head>\n" +
                "\n" +
                "\n" +
                "\t\n" +
                "\t<link class=\"component\" href=\"/sigaa/a4j/s/3_3_3.Finalorg/richfaces/renderkit/html/css/basic_classes.xcss/DATB/eAF7sqpgb-jyGdIAFrMEaw__.jsf\" rel=\"stylesheet\" type=\"text/css\"><link class=\"component\" href=\"/sigaa/a4j/s/3_3_3.Finalorg/richfaces/renderkit/html/css/extended_classes.xcss/DATB/eAF7sqpgb-jyGdIAFrMEaw__.jsf\" rel=\"stylesheet\" type=\"text/css\"><script type=\"text/javascript\">window.RICH_FACES_EXTENDED_SKINNING_ON=true;</script><script src=\"/sigaa/a4j/g/3_3_3.Finalorg/richfaces/renderkit/html/scripts/skinning.js.jsf\" type=\"text/javascript\"></script><script type=\"text/javascript\" src=\"/shared/jsBundles/jawr_loader.js\"></script>\n" +
                "\t<script type=\"text/javascript\">\n" +
                "\t\tJAWR.loader.style('/bundles/css/sigaa_base.css','all');\n" +
                "\t\tJAWR.loader.style('/css/ufrn_relatorio.css','all');\n" +
                "\t\tJAWR.loader.style('/css/ufrn_print.css', 'print');\n" +
                "\n" +
                "\t\tJAWR.loader.script('/bundles/js/sigaa_base.js');\n" +
                "\t</script> <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/shared/cssBundles/gzip_1664710823/bundles/css/sigaa_base.css\">  <link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/shared/cssBundles/gzip_947336466/css/ufrn_relatorio.css\">  <link rel=\"stylesheet\" type=\"text/css\" media=\"print\" href=\"/shared/cssBundles/gzip_17793674/css/ufrn_print.css\">  <script type=\"text/javascript\" src=\"/shared/jsBundles/gzip_1570275411/bundles/js/sigaa_base.js\"> </script> \n" +
                "\t<link rel=\"stylesheet\" type=\"text/css\" media=\"all\" href=\"/sigaa/cssBundles/gzip_1549375585/bundles/css/sigaa.css\">\n" +
                "\n" +
                "\t<link rel=\"stylesheet\" media=\"all\" href=\"/shared/css/ufrn.css\">\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "\n" +
                "\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\n" +
                "<div id=\"relatorio-paisagem-container\">\n" +
                "\n" +
                "\t\t<div>\n" +
                "\t\t\t<div style=\"text-align: center\"><img src=\"https://sig.ifrs.edu.br/shared/img/instituicao/ifrs/brasao_ifrs_public.png\">  </div>\n" +
                "\t\t\t<div style=\"text-align: center; font-weight: bold\">\n" +
                "\t\t\t\tRepública Federativa do Brasil<br>\n" +
                "\t\t\t\tMinistério da Educação<br>\n" +
                "\t\t\t\tSecretaria de Educação Profissional e Tecnológica<br>\n" +
                "\t\t\t\tInstituto Federal de Educação, Ciência e Tecnologia do Rio Grande do Sul<br>\n" +
                "\t\t\t\tLei nº 11.892 de 29 de dezembro de 2008.<br>\n" +
                "\t\t\t</div>\n" +
                "\t\t\t<div class=\"clear\"> </div>\n" +
                "\t\t</div>\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\t<div id=\"relatorio\">\n" +
                "\n" +
                "\t\t<br>\n" +
                "\n" +
                "<link rel=\"stylesheet\" media=\"all\" href=\"/sigaa/css/atestado_matricula.css\" type=\"text/css\">\n" +
                "\n" +
                "\n" +
                "<h3>Atestado de Matrícula</h3>\n" +
                "\n" +
                "\n" +
                "\n" +
                "<table id=\"identificacao\">\n" +
                "\t<tbody><tr>\n" +
                "    \n" +
                "\t\t\n" +
                "\t\t\t<td>\n" +
                "        \t    \n" +
                "         \t   \n" +
                "            \t\n" +
                "            \t\tPeríodo\n" +
                "            \t\n" +
                "            \t\n" +
                "            \tLetivo:\n" +
                "        \t</td>\n" +
                "\t\t\t<td>\n" +
                "\t\t\t\t<strong>\n" +
                "\t\t\t\t\n" +
                "            \t\n" +
                "            \t\n" +
                "            \t\t2024.2\n" +
                "\t\t  \t\t\n" +
                "\t\t  \t\t\n" +
                "\t\t  \t\t</strong>\n" +
                "\t\t  \t\t\n" +
                "            \t\n" +
                "            \t\n" +
                "            \t\t(02/09/2024 à 17/12/2024)\n" +
                "\t\t  \t\t\n" +
                "\t\t  \t\t\n" +
                "\t\t\t</td>\n" +
                "\t\t\n" +
                "\t \n" +
                "\t\t<td>Nível:</td>\n" +
                "\t\t<td><strong>GRADUAÇÃO</strong></td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td width=\"20%\">Matrícula:</td>\n" +
                "\t\t<td width=\"45%\"> <strong>2020007666</strong></td>\n" +
                "\t\t<td width=\"10%\"> Vínculo: </td>\n" +
                "\t\t<td> <strong>REGULAR</strong> </td>\n" +
                "\t</tr>\n" +
                "\t<tr>\n" +
                "\t\t<td>Nome: </td>\n" +
                "\t\t<td colspan=\"3\"><strong>LEONARDO DIAS GOANDETE</strong></td>\n" +
                "\t</tr>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td>Curso: </td>\n" +
                "\t\t\t<td colspan=\"3\"><strong> TECNOLOGIA EM SISTEMAS PARA INTERNET/DE-POA - Porto Alegre - TECNOLÓGICO - MN </strong></td>\n" +
                "\t\t</tr>\n" +
                "\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\t<td>Habilitação: </td>\n" +
                "\t\t\t\t\t<td colspan=\"3\"><strong>TECNÓLOGO(A) EM SISTEMAS PARA INTERNET</strong></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\n" +
                "\t<tr>\n" +
                "\t\t<td>Modalidade:</td>\n" +
                "\t\t<td><strong>Presencial</strong></td>\n" +
                "\t</tr>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "</tbody></table>\n" +
                "\n" +
                "<br>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<table id=\"matriculas\" cellspacing=\"0\">\n" +
                "\t<thead>\n" +
                "\t\t<tr>\n" +
                "\t\t\t\n" +
                "\t\t\t<th align=\"center\">Cód.</th>\n" +
                "\t\t\t<th>Componentes Curriculares/Docentes</th>\n" +
                "\t\t\t<th align=\"center\">Turma</th>\n" +
                "\t\t\t\n" +
                "\t\t\t<th align=\"center\">Status</th>\n" +
                "\t\t\t\n" +
                "\t\t\t<th align=\"center\" class=\"direita\">Horário</th>\n" +
                "\t\t\t\n" +
                "\t\t</tr>\n" +
                "\t</thead>\n" +
                "\t<tbody>\n" +
                "\t\n" +
                "\t\t\n" +
                "\t\t<tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<td class=\"codigo\"> POA-SSI005 </td>\n" +
                "\t\t\t<td valign=\"top\">\n" +
                "\t\t\t\t<span class=\"componente\"> LÍNGUA BRASILEIRA DE SINAIS </span>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td class=\"turma\">01</td>\n" +
                "\t\t\t\n" +
                "\t\t\t<td class=\"status\" style=\"font-variant: small-caps;\">MATRICULADO</td>\n" +
                "\t\t\t\n" +
                "\t\t\t\t<td class=\"horario\">4N1234 (02/09/2024 - 27/09/2024),  7N1234 (28/09/2024 - 29/09/2024),  4N1234 (30/09/2024 - 29/11/2024),  7N1234 (30/11/2024 - 01/12/2024),  4N1234 (02/12/2024 - 17/12/2024) </td>\n" +
                "\t\t\t\n" +
                "\t\t</tr>\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\t<tr>\n" +
                "\t\t\t<td class=\"codigo\">POA-SSI607</td>\n" +
                "\t\t\t<td valign=\"top\">\n" +
                "\t\t\t\t<span class=\"componente\">TRABALHO DE CONCLUSÃO II - CURSO DE TECNOLOGIA EM SISTEMAS PARA INTERNET</span>\n" +
                "\t\t\t\t<span class=\"docente\">\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\t\tORIENTADOR(A): ALEX MARTINS DE OLIVEIRA\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t\t\t<br><b>Forma de Participação:</b> ATIVIDADE DE ORIENTAÇÃO INDIVIDUAL\n" +
                "\t\t\t\t\t\n" +
                "\t\t\t\t</span>\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td class=\"turma\"> -- </td>\n" +
                "\t\t\t<td class=\"status\">MATRICULADO</td>\n" +
                "\t\t\t<td class=\"horario\"> -- </td>\n" +
                "\t\t</tr>\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\n" +
                "\t\t\n" +
                "\t\t<tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<td class=\"codigo\"> POA-SSI606 </td>\n" +
                "\t\t\t<td valign=\"top\">\n" +
                "\t\t\t\t<span class=\"componente\"> TÓPICOS AVANÇADOS </span>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t</td>\n" +
                "\t\t\t<td class=\"turma\">01</td>\n" +
                "\t\t\t\n" +
                "\t\t\t<td class=\"status\" style=\"font-variant: small-caps;\">MATRICULADO</td>\n" +
                "\t\t\t\n" +
                "\t\t\t\t<td class=\"horario\">2N1234 (02/09/2024 - 04/10/2024),  7N1234 (05/10/2024 - 06/10/2024),  2N1234 (07/10/2024 - 17/12/2024) </td>\n" +
                "\t\t\t\n" +
                "\t\t</tr>\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\n" +
                "\t</tbody>\n" +
                "</table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\t<br>\n" +
                "\t<h4>Tabela de Horários:</h4>\n" +
                "\t<table width=\"80%\" id=\"horario\" align=\"center\" cellspacing=\"0\">\n" +
                "\t\t<tbody><tr class=\"titulo\" style=\"background-color: #333366; color: white; font-weight: bold\">\n" +
                "\t\t\t<td align=\"center\">Horários</td>\n" +
                "\t\t\t<td width=\"13%\" align=\"center\">Dom</td>\n" +
                "\t\t\t<td width=\"13%\" align=\"center\">Seg</td>\n" +
                "\t\t\t<td width=\"13%\" align=\"center\">Ter</td>\n" +
                "\t\t\t<td width=\"13%\" align=\"center\">Qua</td>\n" +
                "\t\t\t<td width=\"13%\" align=\"center\">Qui</td>\n" +
                "\t\t\t<td width=\"13%\" align=\"center\">Sex</td>\n" +
                "\t\t\t<td width=\"13%\" align=\"center\">Sab</td>\n" +
                "\t\t</tr>\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">07:30 - 08:20</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_430809\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_430809\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_430809\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_430809\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_430809\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_430809\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_430809\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">08:20 - 09:10</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_430810\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_430810\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_430810\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_430810\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_430810\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_430810\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_430810\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">09:10 - 10:00</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_430811\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_430811\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_430811\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_430811\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_430811\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_430811\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_430811\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">10:10 - 11:00</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_430814\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_430814\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_430814\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_430814\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_430814\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_430814\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_430814\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">11:00 - 11:50</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_430815\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_430815\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_430815\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_430815\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_430815\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_430815\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_430815\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t<tr><td colspan=\"8\" style=\"font-size: 0.1em;\">&nbsp;</td></tr>\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">13:30 - 14:20</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_543197\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_543197\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_543197\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_543197\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_543197\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_543197\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_543197\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">14:20 - 15:10</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_543198\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_543198\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_543198\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_543198\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_543198\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_543198\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_543198\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">15:20 - 16:10</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_543199\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_543199\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_543199\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_543199\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_543199\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_543199\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_543199\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">16:10 - 17:00</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_543200\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_543200\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_543200\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_543200\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_543200\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_543200\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_543200\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">17:00 - 17:50</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_543201\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_543201\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_543201\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_543201\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_543201\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_543201\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_543201\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">18:10 - 19:00</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_543202\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_543202\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_543202\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_543202\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_543202\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_543202\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_543202\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\t<tr><td colspan=\"8\" style=\"font-size: 0.1em;\">&nbsp;</td></tr>\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">19:00 - 19:50</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_379887\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_379887\">POA-SSI606</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_379887\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_379887\">POA-SSI005</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_379887\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_379887\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_379887\">POA-SSI005</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">19:50 - 20:40</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_379888\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_379888\">POA-SSI606</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_379888\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_379888\">POA-SSI005</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_379888\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_379888\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_379888\">POA-SSI005</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">20:50 - 21:40</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_379889\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_379889\">POA-SSI606</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_379889\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_379889\">POA-SSI005</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_379889\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_379889\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_379889\">POA-SSI005</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\t<tr>\n" +
                "\t\t\t\t<td align=\"center\" style=\" \">21:40 - 22:30</td>\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"1_379890\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"2_379890\">POA-SSI606</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"3_379890\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"4_379890\">POA-SSI005</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"5_379890\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"6_379890\">---</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t<td align=\"center\"><span id=\"7_379890\">POA-SSI005</span></td>\n" +
                "\t\t\t\t\n" +
                "\t\t\t</tr>\n" +
                "\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\n" +
                "\t\t\n" +
                "\t</tbody></table>\n" +
                "\n" +
                "\t\n" +
                "\t<br>\n" +
                "\t\n" +
                "\t\n" +
                "\t\n" +
                "\t\t<h4>\n" +
                "\t\t\tObservação: excepcionalmente no(s) dia(s)\n" +
                "\t\t\t\n" +
                "\t\t\t\t30/11/2024\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\t\n" +
                "\t\t\t\n" +
                "\t\t\thaverá aula no sábado.\n" +
                "\t\t</h4>\n" +
                "\t\n" +
                "\t<br>\n" +
                "\t\n" +
                "\t<script type=\"text/javascript\">\n" +
                "\t\tvar dias = \" \";\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('2_379887');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI606';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('2_379888');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI606';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('2_379889');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI606';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('2_379890');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI606';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('4_379887');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI005';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('4_379888');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI005';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('4_379889');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI005';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('4_379890');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI005';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('7_379887');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI005';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('7_379888');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI005';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('7_379889');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI005';\n" +
                "\t\n" +
                "\t\t\tvar elem = document.getElementById('7_379890');\n" +
                "\t\t\tif (elem) elem.innerHTML = 'POA-SSI005';\n" +
                "\t\n" +
                "\t</script>\n" +
                "\n" +
                "\n" +
                "<div id=\"autenticacao\">\n" +
                "\t<h4>ATENÇÃO</h4>\n" +
                "\t<p>\n" +
                "\t\tPara verificar a autenticidade deste documento acesse\n" +
                "\t\t<span>https://sig.ifrs.edu.br/sigaa/documentos/</span> informando a matrícula, a data de emissão e\n" +
                "\t\to código de verificação <span>bcec731feb</span>\n" +
                "\t</p>\n" +
                "\t\n" +
                "</div>\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</div> \n" +
                "\t<div class=\"clear\"> </div>\n" +
                "\t<br>\n" +
                "\t<div id=\"relatorio-rodape\">\n" +
                "\t\t<p>\n" +
                "\t\t\t<table width=\"100%\">\n" +
                "\t\t\t\t<tbody><tr>\n" +
                "\t\t\t\t\t<td class=\"voltar\" align=\"left\">\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t \t<a href=\"javascript:history.back();\"> Voltar </a>\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t<td width=\"70%\" align=\"center\">\n" +
                "\t\t\t\t\tSIGAA | Diretoria de Tecnologia da Informação - - | Copyright © 2006-2024 - IFRS - sigprod-m3-host.instTimer\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t<td class=\"naoImprimir\" align=\"right\">\n" +
                "\t\t\t\t\t\t<a onclick=\"javascript:window.print();\" href=\"#\">Imprimir</a>\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t\t<td class=\"naoImprimir\" align=\"right\">\n" +
                "\t\t\t\t\t\t<a onclick=\"javascript:window.print();\" href=\"#\">\t\t\t\t\t\t\t\n" +
                "\t\t\t\t\t\t\t<img alt=\"Imprimir\" title=\"Imprimir\" src=\"/shared/javascript/ext-1.1/docs/resources/print.gif\">\n" +
                "\t\t\t\t\t\t</a>\n" +
                "\t\t\t\t\t</td>\n" +
                "\t\t\t\t</tr>\n" +
                "\t\t\t</tbody></table>\n" +
                "\t\t</p>\n" +
                "\n" +
                "\t</div>\n" +
                "</div>  \n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "<!-- MYFACES JAVASCRIPT -->\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "</body></html>";


        Document doc = Jsoup.parse(html);


        updateElementById(doc, "1_379887", "POA-SSI606");

        doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        String xhtml = doc.html();

        String outputPdfPath = "output.pdf";

        // Converte o HTML para PDF e salva no sistema de arquivos
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             FileOutputStream fileOutputStream = new FileOutputStream(outputPdfPath)) {

            ITextRenderer renderer = new ITextRenderer();
            renderer.setDocumentFromString(xhtml);  // Usa o HTML corrigido
            renderer.layout();
            renderer.createPDF(byteArrayOutputStream);  // Gera o PDF no ByteArrayOutputStream
            byteArrayOutputStream.flush();

            // Grava o PDF no arquivo
            fileOutputStream.write(byteArrayOutputStream.toByteArray());
            System.out.println("PDF salvo com sucesso em: " + outputPdfPath);

            // Opcional: converte o PDF para Base64 se necessário
            String pdfBase64 = Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
            System.out.println("PDF em Base64: " + pdfBase64);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao renderizar o PDF: " + e.getMessage());
        }
    }




    private static void updateElementById(Document doc, String elementId, String newValue) {
        Element elem = doc.getElementById(elementId);
        if (elem != null) {
            elem.text(newValue);
        }
    }
}
