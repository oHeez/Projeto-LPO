package util;

public class ValidacaoUtil {
    
    public static boolean validarCPF(String cpf) {
        if (cpf == null || cpf.trim().isEmpty()) {
            return false;
        }
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            return false;
        }
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false;
        }
        try {
            int[] numeros = new int[11];
            for (int i = 0; i < 11; i++) {
                numeros[i] = Integer.parseInt(cpfLimpo.substring(i, i + 1));
            }
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += numeros[i] * (10 - i);
            }
            int digito1 = 11 - (soma % 11);
            if (digito1 >= 10) digito1 = 0;
            if (digito1 != numeros[9]) {
                return false;
            }
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += numeros[i] * (11 - i);
            }
            int digito2 = 11 - (soma % 11);
            if (digito2 >= 10) digito2 = 0;
            return digito2 == numeros[10];
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    public static boolean validarEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String regex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(regex);
    }
    
    public static boolean validarTelefone(String telefone) {
        if (telefone == null || telefone.trim().isEmpty()) {
            return false;
        }
        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
        if (telefoneLimpo.length() == 10 || telefoneLimpo.length() == 11) {
            return true;
        }
        return false;
    }
    
    public static String formatarCPF(String cpf) {
        if (!validarCPF(cpf)) {
            return null;
        }
        
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        if (cpfLimpo.length() != 11) {
            return null;
        }
        
        return cpfLimpo.substring(0, 3) + "." + 
               cpfLimpo.substring(3, 6) + "." + 
               cpfLimpo.substring(6, 9) + "-" + 
               cpfLimpo.substring(9, 11);
    }
    
    public static String formatarTelefone(String telefone) {
        if (!validarTelefone(telefone)) {
            return null;
        }
        
        String telefoneLimpo = telefone.replaceAll("[^0-9]", "");
        
        if (telefoneLimpo.length() == 11) {
            return "(" + telefoneLimpo.substring(0, 2) + ") " + 
                   telefoneLimpo.substring(2, 7) + "-" + 
                   telefoneLimpo.substring(7, 11);
        } else if (telefoneLimpo.length() == 10) {
            return "(" + telefoneLimpo.substring(0, 2) + ") " + 
                   telefoneLimpo.substring(2, 6) + "-" + 
                   telefoneLimpo.substring(6, 10);
        }
        return null;
    }
    
    public static boolean validarStringNaoVazia(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }
    
    public static boolean validarIntervalo(int numero, int min, int max) {
        return numero >= min && numero <= max;
    }
    
    public static boolean validarIntervalo(double numero, double min, double max) {
        return numero >= min && numero <= max;
    }
}

