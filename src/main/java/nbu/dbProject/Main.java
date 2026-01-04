package nbu.dbProject;

import nbu.dbProject.dao.CompanyDao;
import nbu.dbProject.entity.Company;

public class Main {
    public static void main(String[] args) {
        Company company = new Company();
        company.setName("Jakarta");
        CompanyDao.createCompany(company);
    }
}