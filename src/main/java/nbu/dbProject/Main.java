package nbu.dbProject;

import nbu.dbProject.entity.Company;

public class Main {
    public static void main(String[] args) {
        Company company = new Company("Jakarta123", "4234567890", "asdfff", "0895644331");
        company.setName("Jakarta");
//        CompanyDao.createCompany(company);

        System.out.println(company);
    }
}